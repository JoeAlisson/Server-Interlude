package org.l2j.authserver.controller;

import com.l2jbr.commons.Base64;
import com.l2jbr.commons.Config;
import com.l2jbr.commons.database.AccountRepository;
import com.l2jbr.commons.database.model.Account;
import com.l2jbr.commons.util.Rnd;
import com.l2jbr.commons.util.Util;
import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.GameServerManager;
import org.l2j.authserver.network.AuthClient;
import org.l2j.authserver.network.GameServerConnection;
import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.authserver.network.packet.game2auth.ServerStatus;
import org.l2j.authserver.network.packet.auth2client.LoginFail;
import org.l2j.authserver.settings.AuthServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.l2jbr.commons.database.DatabaseAccess.getRepository;
import static com.l2jbr.commons.util.Util.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Logger loginLogger = LoggerFactory.getLogger("loginHistory");
    private static final int LOGIN_TIMEOUT = 60 * 1000;
    private static final int BLOWFISH_KEYS = 20;

    private static AuthController _instance;

    private final Set<AuthClient> connectedClients = new HashSet<>();
    private final Map<String, AuthClient> authedClients = new ConcurrentHashMap<>();
    private final Map<String, FailedLoginAttempt> _hackProtection = new HashMap<>();
    private final BanManager banManager;
    private ScheduledFuture<?> scheduledPurge;

    private ScrambledKeyPair[] _keyPairs;
    private byte[][] _blowfishKeys;

    private AuthController() throws GeneralSecurityException {
        logger.info("Loading Auth Controller...");
        banManager = BanManager.load();

        initializeScrambledKeys();
        generateBlowFishKeys();
    }

    public static void load() throws GeneralSecurityException {
        if (isNull(_instance)) {
            _instance = new AuthController();
        }
    }

    private void initializeScrambledKeys() throws GeneralSecurityException {
        var keygen = KeyPairGenerator.getInstance("RSA");
        var spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);

        _keyPairs = new ScrambledKeyPair[10];

        for (int i = 0; i < 10; i++) {
            _keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
        }
        logger.info("Cached 10 KeyPairs for RSA communication");

        testCipher((RSAPrivateKey) _keyPairs[0]._pair.getPrivate());
    }

    private void testCipher(RSAPrivateKey key) throws GeneralSecurityException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
        rsaCipher.init(Cipher.DECRYPT_MODE, key);
    }

    private void generateBlowFishKeys() {
        _blowfishKeys = new byte[BLOWFISH_KEYS][16];

        for (int i = 0; i < BLOWFISH_KEYS; i++) {
            for (int j = 0; j < _blowfishKeys[i].length; j++) {
                _blowfishKeys[i][j] = (byte) (Rnd.nextInt(255) + 1);
            }
        }
        logger.info("Stored {} keys for Blowfish communication", _blowfishKeys.length);
    }

    private byte[] getBlowfishKey() {
        return _blowfishKeys[(int) (Math.random() * BLOWFISH_KEYS)];
    }

    public void registerClient(AuthClient client) {
        client.setKeyPar(getScrambledRSAKeyPair());
        client.setBlowfishKey(getBlowfishKey());
        client.setSessionId(Rnd.nextInt());
        var cripter = new AuthCrypt();
        cripter.setKey(client.getBlowfishKey());
        client.setCrypter(cripter);

        if(isNull(scheduledPurge) || scheduledPurge.isCancelled()) {
            scheduledPurge = ThreadPoolManager.getInstance().scheduleAtFixedRate(PurgeThread::new, LOGIN_TIMEOUT, 2 * LOGIN_TIMEOUT);
        }
    }


    public void assignSessionKeyToClient(String account, AuthClient client) {
        var key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
        client.setSessionKey(key);
        authedClients.put(account, client);
    }

    public void removeAuthedClient(String account) {
        if(isNullOrEmpty(account)) {
            return;
        }

        var client = authedClients.remove(account);
        if(nonNull(client)) {
            removeClient(client);
        }
    }

    public AuthClient getAuthedClient(String account) {
        return authedClients.get(account);
    }

    public AuthResult tryAuthLogin(String account, String password, AuthClient client) {
        AuthResult ret = AuthResult.INVALID_PASSWORD;
        if (loginValid(account, password, client)) {
            // login was successful, verify presence on Gameservers
            ret = AuthResult.ALREADY_ON_GS;
            if (!isAccountInAnyGameServer(account)) {
                // account isnt on any GS verify LS itself
                ret = AuthResult.ALREADY_ON_LS;

                // dont allow 2 simultaneous login
                synchronized (authedClients) {
                    if (!authedClients.containsKey(account)) {
                        authedClients.put(account, client);
                        ret = AuthResult.AUTH_SUCCESS;
                    }
                }
            }
        } else {
            if (client.getAccessLevel() < 0) {
                ret = AuthResult.ACCOUNT_BANNED;
            }
        }
        return ret;
    }

    public boolean isBannedAddress(String address) {
        return banManager.isBanned(address);
    }

    public SessionKey getKeyForAccount(String account) {
        AuthClient client = authedClients.get(account);
        if (nonNull(client)) {
            return client.getSessionKey();
        }
        return null;
    }

    public int getOnlinePlayerCount(int serverId) {
        GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(serverId);
        if ((gsi != null) && gsi.isAuthed()) {
            return gsi.getOnlinePlayersCount();
        }
        return 0;
    }

    private boolean isAccountInAnyGameServer(String account) {
        Collection<GameServerInfo> serverList = GameServerManager.getInstance().getRegisteredGameServers().values();
        for (GameServerInfo gsi : serverList) {
            GameServerConnection gst = gsi.getGameServerThread();
            if ((nonNull(gst)) && gst.hasAccountOnGameServer(account)) {
                return true;
            }
        }
        return false;
    }

    public GameServerInfo getAccountOnGameServer(String account) {
        Collection<GameServerInfo> serverList = GameServerManager.getInstance().getRegisteredGameServers().values();
        for (GameServerInfo gsi : serverList) {
            GameServerConnection gst = gsi.getGameServerThread();
            if ((gst != null) && gst.hasAccountOnGameServer(account)) {
                return gsi;
            }
        }
        return null;
    }

    public int getTotalOnlinePlayerCount() {
        int total = 0;
        Collection<GameServerInfo> serverList = GameServerManager.getInstance().getRegisteredGameServers().values();
        for (GameServerInfo gsi : serverList) {
            if (gsi.isAuthed()) {
                total += gsi.getOnlinePlayersCount();
            }
        }
        return total;
    }

    public int getMaxAllowedOnlinePlayers(int id) {
        GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(id);
        if (gsi != null) {
            return gsi.getMaxPlayers();
        }
        return 0;
    }

    public boolean isLoginPossible(AuthClient client, int serverId) {
        GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(serverId);
        int access = client.getAccessLevel();
        if (nonNull(gsi) && gsi.isAuthed()) {
            boolean loginOk = ((gsi.getOnlinePlayersCount() < gsi.getMaxPlayers()) && (gsi.getStatus() != ServerStatus.STATUS_GM_ONLY)) || (access >= Config.GM_MIN);

            if (loginOk && (client.getLastServer() != serverId)) {
                AccountRepository accountRepository = getRepository(AccountRepository.class);
                if(accountRepository.updateLastServer(client.getAccount(), serverId) < 1) {
                    logger.warn("Could not set lastServer of account {} ", client.getAccount());
                }
            }
            return loginOk;
        }
        return false;
    }

    public void setAccountAccessLevel(String login, short acessLevel) {
        if(getRepository(AccountRepository.class).updateAccessLevel(login, acessLevel) < 1) {
            logger.warn("Could not set accessLevel of account {}", login);
        }
    }

    private ScrambledKeyPair getScrambledRSAKeyPair() {
        return _keyPairs[Rnd.nextInt(10)];
    }

    private boolean loginValid(String user, String password, AuthClient client) {
        boolean ok = false;
        String address = client.getHostAddress();

        // player disconnect meanwhile
        if (address.isEmpty()) {
            return false;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] raw = password.getBytes(StandardCharsets.UTF_8);
            byte[] hash = md.digest(raw);

            var repository = getRepository(AccountRepository.class);
            var optionalAccount = repository.findById(user);

            if (optionalAccount.isPresent()) {
                var account = optionalAccount.get();

                if (account.isBanned()) {
                    client.setAccessLevel(account.getAccessLevel());
                    return false;
                }

                byte[] expected = Base64.decode(account.getPassword());

                ok = Arrays.equals(expected, hash);

                if (ok) {
                    client.setAccessLevel(account.getAccessLevel());
                    client.setLastServer(account.getLastServer());
                    account.setLastActive(currentTimeMillis());
                    account.setLastIP(address);
                    repository.save(account);
                }
            } else if (AuthServerSettings.isAutoCreateAccount()) {
                if ((user.length() >= 2) && (user.length() <= 14)) {
                    String pwd = Base64.encodeBytes(hash);
                    long lastActive = currentTimeMillis();
                    Account account = new Account(user, pwd, lastActive, address);

                    if (repository.save(account).isPersisted()) {
                        logger.debug("created new account for {}", user);
                        return true;
                    }
                }

                logger.debug("Invalid username creation/use attempt: {}", user);
                return false;
            } else {
                logger.debug("account missing for user {}", user);
            }
        } catch (Exception e) {
            logger.warn("Could not check password", e);
            ok = false;
        }

        if (!ok) {
            loginLogger.info("Failed login {} : {}", user, address);

            FailedLoginAttempt failedAttempt = _hackProtection.get(address);
            int failedCount;
            if (isNull(failedAttempt)) {
                _hackProtection.put(address, new FailedLoginAttempt(password));
                failedCount = 1;
            } else {
                failedAttempt.increaseCounter(password);
                failedCount = failedAttempt.getCount();
            }

            if (failedCount >= AuthServerSettings.loginTryBeforeBan()) {
                logger.info("Banning {} for seconds due to {} invalid user/pass attempts", address, AuthServerSettings.loginBlockAfterBan(), failedCount);
                 banManager.addBannedAdress(address, currentTimeMillis() + AuthServerSettings.loginBlockAfterBan() * 1000);
            }
        } else {
            _hackProtection.remove(address);
            loginLogger.info("Success Login {} : {}", user, address);
        }

        return ok;
    }

    public static AuthController getInstance() {
        return _instance;
    }

    public void removeClient(AuthClient client) {
        if(nonNull(client)) {
            connectedClients.remove(client);
        }
    }

    private class FailedLoginAttempt {

        private int _count = 1;
        private long _lastAttempTime;
        private String _lastPassword;

        FailedLoginAttempt(String lastPassword) {
            _lastAttempTime = currentTimeMillis();
            _lastPassword = lastPassword;
        }

        void increaseCounter(String password) {
            if (!_lastPassword.equals(password)) {
                // check if theres a long time since last wrong try
                if ((currentTimeMillis() - _lastAttempTime) < (300 * 1000)) {
                    _count++;
                } else {
                    _count = 1;

                }
                _lastPassword = password;
                _lastAttempTime = currentTimeMillis();
            } else {
                // trying the same password is not brute force
                _lastAttempTime = currentTimeMillis();
            }
        }

        int getCount() {
            return _count;
        }
    }

    private class PurgeThread implements Runnable{
        @Override
        public void run() {
            Set<AuthClient> toRemove =  new HashSet<>();
            synchronized (connectedClients) {
                connectedClients.forEach(client -> {
                    if (isNull(client) || client.getConnectionStartTime() + LOGIN_TIMEOUT >= currentTimeMillis()) {
                        toRemove.add(client);
                    }
                });
                connectedClients.removeAll(toRemove);
                if(connectedClients.isEmpty()) {
                    scheduledPurge.cancel(false);
                }
            }

            toRemove.stream().filter(Objects::nonNull).forEach(authClient -> authClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED_TRYA1));
        }
    }
}
