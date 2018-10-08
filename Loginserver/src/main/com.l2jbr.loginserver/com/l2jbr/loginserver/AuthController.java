package com.l2jbr.loginserver;

import com.l2jbr.commons.Base64;
import com.l2jbr.commons.Config;
import com.l2jbr.commons.database.AccountRepository;
import com.l2jbr.commons.database.model.Account;
import com.l2jbr.commons.util.Rnd;
import com.l2jbr.loginserver.GameServerTable.GameServerInfo;
import com.l2jbr.loginserver.network.GameServerConnection;
import com.l2jbr.loginserver.network.AuthClient;
import com.l2jbr.loginserver.network.SessionKey;
import com.l2jbr.loginserver.network.crypt.LoginCrypt;
import com.l2jbr.loginserver.network.crypt.ScrambledKeyPair;
import com.l2jbr.loginserver.network.gameserverpackets.ServerStatus;
import com.l2jbr.loginserver.network.serverpackets.LoginFail.LoginFailReason;
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

import static com.l2jbr.commons.database.DatabaseAccess.getRepository;
import static com.l2jbr.loginserver.settings.LoginServerSettings.*;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Logger loginLogger = LoggerFactory.getLogger("loginHistory");
    private static final int LOGIN_TIMEOUT = 60 * 1000;
    private static final int BLOWFISH_KEYS = 20;

    private static AuthController _instance;

    private final Map<String, AuthClient> _loginServerClients = new ConcurrentHashMap<>();
    private final Map<String, FailedLoginAttempt> _hackProtection = new HashMap<>();
    private final BanManager banManager;

    private ScrambledKeyPair[] _keyPairs;
    private byte[][] _blowfishKeys;

    private AuthController() throws GeneralSecurityException {
        logger.info("Loading Auth Controller...");
        banManager = BanManager.load();

        initializeScrambledKeys();
        generateBlowFishKeys();
    }

    static void load() throws GeneralSecurityException {
        if (isNull(_instance)) {
            _instance = new AuthController();
        }
    }

    private void initializeScrambledKeys() throws GeneralSecurityException {
        var keygen = KeyPairGenerator.getInstance("RSA");
        var spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);

        _keyPairs = new ScrambledKeyPair[10];

        // generate the initial set of keys
        for (int i = 0; i < 10; i++) {
            _keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
        }
        logger.info("Cached 10 KeyPairs for RSA communication");

        testCipher((RSAPrivateKey) _keyPairs[0]._pair.getPrivate());
    }

    /**
     * This is mostly to force the initialization of the Crypto Implementation, avoiding it being done on runtime when its first needed.<BR>
     * In short it avoids the worst-case execution time on runtime by doing it on loading.
     *
     * @param key Any private RSA Key just for testing purposes.
     * @throws GeneralSecurityException if a underlying exception was thrown by the Cipher
     */
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
        logger.info("Stored " + _blowfishKeys.length + " keys for Blowfish communication");
    }

    /**
     * @return Returns a random key
     */
    public byte[] getBlowfishKey() {
        return _blowfishKeys[(int) (Math.random() * BLOWFISH_KEYS)];
    }


    public SessionKey assignSessionKeyToClient(String account, AuthClient client) {
        SessionKey key;

        key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
        _loginServerClients.put(account, client);
        return key;
    }

    public void removeAuthedClient(String account) {
        _loginServerClients.remove(account);
    }

    public AuthClient getAuthedClient(String account) {
        return _loginServerClients.get(account);
    }

    public AuthLoginResult tryAuthLogin(String account, String password, AuthClient client) {
        AuthLoginResult ret = AuthLoginResult.INVALID_PASSWORD;
        if (loginValid(account, password, client)) {
            // login was successful, verify presence on Gameservers
            ret = AuthLoginResult.ALREADY_ON_GS;
            if (!isAccountInAnyGameServer(account)) {
                // account isnt on any GS verify LS itself
                ret = AuthLoginResult.ALREADY_ON_LS;

                // dont allow 2 simultaneous login
                synchronized (_loginServerClients) {
                    if (!_loginServerClients.containsKey(account)) {
                        _loginServerClients.put(account, client);
                        ret = AuthLoginResult.AUTH_SUCCESS;
                    }
                }
            }
        } else {
            if (client.getAccessLevel() < 0) {
                ret = AuthLoginResult.ACCOUNT_BANNED;
            }
        }
        return ret;
    }

    public boolean isBannedAddress(String address) {
        return banManager.isBanned(address);
    }

    public SessionKey getKeyForAccount(String account) {
        AuthClient client = _loginServerClients.get(account);
        if (nonNull(client)) {
            return client.getSessionKey();
        }
        return null;
    }

    public int getOnlinePlayerCount(int serverId) {
        GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(serverId);
        if ((gsi != null) && gsi.isAuthed()) {
            return gsi.getOnlinePlayersCount();
        }
        return 0;
    }

    public boolean isAccountInAnyGameServer(String account) {
        Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
        for (GameServerInfo gsi : serverList) {
            GameServerConnection gst = gsi.getGameServerThread();
            if ((nonNull(gst)) && gst.hasAccountOnGameServer(account)) {
                return true;
            }
        }
        return false;
    }

    public GameServerInfo getAccountOnGameServer(String account) {
        Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
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
        Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
        for (GameServerInfo gsi : serverList) {
            if (gsi.isAuthed()) {
                total += gsi.getOnlinePlayersCount();
            }
        }
        return total;
    }

    public int getMaxAllowedOnlinePlayers(int id) {
        GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(id);
        if (gsi != null) {
            return gsi.getMaxPlayers();
        }
        return 0;
    }


    public boolean isLoginPossible(AuthClient client, int serverId) {
        GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(serverId);
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

    /**
     * <p>
     * This method returns one of the cached {@link ScrambledKeyPair ScrambledKeyPairs} for communication with Login Clients.
     * </p>
     *
     * @return a scrambled keypair
     */
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
            } else if (isAutoCreateAccount()) {
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

            if (failedCount >= loginTryBeforeBan()) {
                logger.info("Banning {} for seconds due to {} invalid user/pass attempts", address, loginBlockAfterBan(), failedCount);
                 banManager.addBannedAdress(address, currentTimeMillis() + loginBlockAfterBan() * 1000);
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

    public void registerClient(AuthClient client) {
        client.setKeyPar(getScrambledRSAKeyPair());
        client.setBlowfishKey(getBlowfishKey());
        client.setSessionId(Rnd.nextInt());
        client.setCrypter(new LoginCrypt());

    }

    public enum AuthLoginResult {
        INVALID_PASSWORD,
        ACCOUNT_BANNED,
        ALREADY_ON_LS,
        ALREADY_ON_GS,
        AUTH_SUCCESS
    }

    class FailedLoginAttempt {
        private int _count;
        private long _lastAttempTime;
        private String _lastPassword;

        FailedLoginAttempt(String lastPassword) {
            _count = 1;
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

    class PurgeThread extends Thread {
        @Override
        public void run() {
            for (; ; ) {

                synchronized (_loginServerClients) {
                    for (Map.Entry<String, AuthClient> e : _loginServerClients.entrySet()) {
                        AuthClient client = e.getValue();
                        if ((client.getConnectionStartTime() + LOGIN_TIMEOUT) >= currentTimeMillis()) {
                            client.close(LoginFailReason.REASON_ACCESS_FAILED);
                        }
                    }
                }

                try {
                    Thread.sleep(2 * LOGIN_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
