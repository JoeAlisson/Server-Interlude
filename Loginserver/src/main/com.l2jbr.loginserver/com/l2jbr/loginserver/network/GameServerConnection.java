package com.l2jbr.loginserver.network;

import com.l2jbr.commons.crypt.NewCrypt;
import com.l2jbr.loginserver.AuthServer;
import com.l2jbr.loginserver.GameServerTable;
import com.l2jbr.loginserver.GameServerTable.GameServerInfo;
import com.l2jbr.loginserver.network.gameserverpackets.*;
import com.l2jbr.loginserver.network.loginserverpackets.*;
import com.l2jbr.loginserver.network.serverpackets.ServerBasePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import static com.l2jbr.commons.util.Util.printData;
import static com.l2jbr.loginserver.settings.LoginServerSettings.acceptNewGameServerEnabled;
import static java.lang.Byte.toUnsignedInt;
import static java.util.Objects.nonNull;

/**
 * @author -Wooden-
 * @author KenM
 */
public class GameServerConnection extends Thread {
    protected static final Logger logger = LoggerFactory.getLogger(GameServerConnection.class);

    private final Socket socket;
    private InputStream _in;
    private OutputStream _out;
    private final RSAPublicKey _publicKey;
    private final RSAPrivateKey _privateKey;
    private NewCrypt _blowfish;

    private final String ip;

    private GameServerInfo _gsi;

    private final Set<String> _accountsOnGameServer = new HashSet<>();

    public GameServerConnection(Socket con) {
        socket = con;
        ip = con.getInetAddress().getHostAddress();
        try {
            _in = socket.getInputStream();
            _out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        KeyPair pair = GameServerTable.getInstance().getKeyPair();
        _privateKey = (RSAPrivateKey) pair.getPrivate();
        _publicKey = (RSAPublicKey) pair.getPublic();
        _blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
        start();
    }

    @Override
    public void run() {
        String _connectionIPAddress = socket.getInetAddress().getHostAddress();
        InitLS startPacket = new InitLS(_publicKey.getModulus().toByteArray());
        try {
            sendPacket(startPacket);

            int lengthHi;
            int lengthLo;
            int length;
            boolean checksumOk;

            while (true) {
                lengthLo = _in.read();
                lengthHi = _in.read();
                length = (lengthHi << 8) + lengthLo;

                if ((lengthHi < 0) || socket.isClosed()) {
                    logger.debug("LoginServerThread: Login terminated the socket.");
                    break;
                }

                int dataSize = length-2;
                byte[] data = new byte[dataSize];

                int receivedBytes = readData(dataSize, data);

                if (receivedBytes != (length - 2)) {
                    logger.warn("Incomplete Packet is sent to the server, closing socket.(LS)");
                    break;
                }

                // decrypt if we have a key
                data = _blowfish.decrypt(data);
                checksumOk = NewCrypt.verifyChecksum(data);
                if (!checksumOk) {
                    logger.warn("Incorrect packet checksum, closing socket (LS)");
                    return;
                }

                logger.debug("[Receive]\n {}", printData(data));

                handlePacket(data);
            }
        } catch (IOException e) {
            String serverName = _connectionIPAddress;

            if(getServerId() != -1) {
                serverName = String.format("[%d] %s", getServerId(), GameServerTable.getInstance().getServerNameById(getServerId()));
            }

            String msg = String.format("GameServer %s : Connection Lost: %s", serverName , e.getLocalizedMessage());
            logger.info(msg);
            broadcastToStatusServer(msg);
        } finally {
            if (isAuthed()) {
                _gsi.setDown();
                logger.info("Server [{}] {} is now set as disconnect", getServerId(), GameServerTable.getInstance().getServerNameById(getServerId()));
            }
            AuthServer.removeGameserver(this, ip);
        }
    }

    private void handlePacket(byte[] data) throws IOException {
        int packetType = toUnsignedInt(data[0]);
        switch (packetType) {
            case 0x0:
                onReceiveBlowfishKey(data);
                break;
            case 0x1:
                onGameServerAuth(data);
                break;
            case 0x2:
                onReceivePlayerInGame(data);
                break;
            case 0x3:
                onReceivePlayerLogOut(data);
                break;
            case 0x4:
                onReceiveChangeAccessLevel(data);
                break;
            case 0x5:
                onReceivePlayerAuthRequest(data);
                break;
            case 0x6:
                onReceiveServerStatus(data);
                break;
            default:
                logger.warn("Unknown Opcode ({}) from GameServer, closing socket.", Integer.toHexString(packetType).toUpperCase());
                forceClose(LoginServerFail.NOT_AUTHED);
        }
    }

    private int readData(int dataSize, byte[] data) throws IOException {
        int receivedBytes = 0;
        int newBytes;
        while (receivedBytes < dataSize) {
            newBytes = _in.read(data, receivedBytes, dataSize - receivedBytes);
            if(newBytes == -1) {
                break;
            }
            receivedBytes = receivedBytes + newBytes;
        }
        return receivedBytes;
    }

    private void onReceiveBlowfishKey(byte[] data) {
        BlowFishKey bfk = new BlowFishKey(data, _privateKey);
        byte[] _blowfishKey = bfk.getKey();
        _blowfish = new NewCrypt(_blowfishKey);

        logger.debug("New BlowFish key received, Blowfih Engine initialized:");
    }

    private void onGameServerAuth(byte[] data) throws IOException {
        GameServerAuth gsa = new GameServerAuth(data);
        logger.debug("Auth request received");

        handleAuthProcess(gsa);
        if (isAuthed()) {
            AuthResponse ar = new AuthResponse(_gsi.getId());
            sendPacket(ar);
            logger.debug("Authed: id: {}", _gsi.getId());
            broadcastToStatusServer(String.format("GameServer [%d] %s is connected", getServerId(), GameServerTable.getInstance().getServerNameById(getServerId())));
        }
    }

    private void onReceivePlayerInGame(byte[] data) {
        if (isAuthed()) {
            PlayerInGame pig = new PlayerInGame(data);
            List<String> newAccounts = pig.getAccounts();
            for (String account : newAccounts) {
                _accountsOnGameServer.add(account);
                logger.debug("Account {} logged in GameServer: [{}] {}",  account, getServerId(), GameServerTable.getInstance().getServerNameById(getServerId()));

                broadcastToStatusServer(String.format("Account %s logged in GameServer %d", account, getServerId()));
            }
        } else {
            forceClose(LoginServerFail.NOT_AUTHED);
        }
    }

    private void onReceivePlayerLogOut(byte[] data) {
        if (isAuthed()) {
            PlayerLogout plo = new PlayerLogout(data);
            _accountsOnGameServer.remove(plo.getAccount());
            logger.debug("Player {} logged out from gameserver [{}] {}", plo.getAccount(), getServerId(), GameServerTable.getInstance().getServerNameById(getServerId()));

            broadcastToStatusServer(String.format("Player %s disconnect from GameServer  %d", plo.getAccount(), getServerId()));
        } else {
            forceClose(LoginServerFail.NOT_AUTHED);
        }
    }

    private void onReceiveChangeAccessLevel(byte[] data) {
        if (isAuthed()) {
            ChangeAccessLevel cal = new ChangeAccessLevel(data);
            LoginController.getInstance().setAccountAccessLevel(cal.getAccount(), (short) cal.getLevel());
            logger.info("Changed {} access level to {}", cal.getAccount(), cal.getLevel());
        } else {
            forceClose(LoginServerFail.NOT_AUTHED);
        }
    }

    private void onReceivePlayerAuthRequest(byte[] data) throws IOException {
        if (isAuthed()) {
            PlayerAuthRequest par = new PlayerAuthRequest(data);

            logger.debug("auth request received for Player {}", par.getAccount());

            SessionKey key = LoginController.getInstance().getKeyForAccount(par.getAccount());

            PlayerAuthResponse authResponse;
            if (Objects.equals(par.getKey(), key)) {
                logger.debug("auth request: OK");
                LoginController.getInstance().removeAuthedLoginClient(par.getAccount());
                authResponse = new PlayerAuthResponse(par.getAccount(), true);
            } else {
                logger.debug("auth request: NO");
                logger.debug("session key from self: {}", key);
                logger.debug("session key sent: {}", par.getKey());

                authResponse = new PlayerAuthResponse(par.getAccount(), false);
            }
            sendPacket(authResponse);
        } else {
            forceClose(LoginServerFail.NOT_AUTHED);
        }
    }

    private void onReceiveServerStatus(byte[] data) {
        if (isAuthed()) {
            logger.debug("ServerStatus received");

            new ServerStatus(data, getServerId());
        } else {
            forceClose(LoginServerFail.NOT_AUTHED);
        }
    }

    private void handleAuthProcess(GameServerAuth gameServerAuth) {
        GameServerTable gameServerTable = GameServerTable.getInstance();

        int id = gameServerAuth.getDesiredID();
        byte[] hexId = gameServerAuth.getHexID();

        GameServerInfo gsi = gameServerTable.getRegisteredGameServerById(id);
        if (nonNull(gsi)) {
            if (Arrays.equals(gsi.getHexId(), hexId)) {
                if (gsi.isAuthed()) {
                    forceClose(LoginServerFail.REASON_ALREADY_LOGGED);
                } else {
                    attachGameServerInfo(gsi, gameServerAuth);
                }
            } else {
                // there is already a server registered with the desired id and different hex id
                // try to register this one with an alternative id
                if (acceptNewGameServerEnabled() && gameServerAuth.acceptAlternateID()) {
                    gsi = new GameServerInfo(id, hexId, this);
                    if (gameServerTable.registerWithFirstAvaliableId(gsi)) {
                        attachGameServerInfo(gsi, gameServerAuth);
                        gameServerTable.registerServerOnDB(gsi);
                    } else {
                        forceClose(LoginServerFail.REASON_NO_FREE_ID);
                    }
                } else {
                    forceClose(LoginServerFail.REASON_WRONG_HEXID);
                }
            }
        } else {
            if (acceptNewGameServerEnabled()) {
                gsi = new GameServerInfo(id, hexId, this);
                if (gameServerTable.register(id, gsi)) {
                    attachGameServerInfo(gsi, gameServerAuth);
                    gameServerTable.registerServerOnDB(gsi);
                } else {
                    forceClose(LoginServerFail.REASON_ID_RESERVED);
                }
            } else {
                forceClose(LoginServerFail.REASON_WRONG_HEXID);
            }
        }
    }

    public boolean hasAccountOnGameServer(String account) {
        return _accountsOnGameServer.contains(account);
    }

    public int getPlayerCount() {
        return _accountsOnGameServer.size();
    }

    /**
     * Attachs a GameServerInfo to this Thread <li>Updates the GameServerInfo values based on GameServerAuth packet</li> <li><b>Sets the GameServerInfo as Authed</b></li>
     *
     * @param gsi            The GameServerInfo to be attached.
     * @param gameServerAuth The server info.
     */
    private void attachGameServerInfo(GameServerInfo gsi, GameServerAuth gameServerAuth) {
        setGameServerInfo(gsi);
        gsi.setGameServerThread(this);
        gsi.setPort(gameServerAuth.getPort());
        setHosts(gameServerAuth.getExternalHost(), gameServerAuth.getInternalHost());
        gsi.setMaxPlayers(gameServerAuth.getMaxPlayers());
        gsi.setAuthed(true);
    }

    private void forceClose(int reason) {
        LoginServerFail lsf = new LoginServerFail(reason);
        try {
            sendPacket(lsf);
        } catch (IOException e) {
            logger.error("GameServerConnection: Failed kicking banned server. Reason: {}", e.getLocalizedMessage());
        }

        try {
            socket.close();
        } catch (IOException e) {
            logger.debug("GameServerConnection: Failed disconnecting banned server, server already disconnect.");
        }
    }

    private void sendPacket(ServerBasePacket sl) throws IOException {
        byte[] data = sl.getContent();
        NewCrypt.appendChecksum(data);

        logger.debug("[Send] {}:\n {} ", sl.getClass().getSimpleName(), printData(data));
        data = _blowfish.crypt(data);

        int len = data.length + 2;
        synchronized (_out) {
            _out.write(len & 0xff);
            _out.write((len >> 8) & 0xff);
            _out.write(data);
            _out.flush();
        }
    }

    private void broadcastToStatusServer(String msg) {
        AuthServer.sendMessageToStatusServer(msg);
    }

    public void kickPlayer(String account) {
        KickPlayer kp = new KickPlayer(account);
        try {
            sendPacket(kp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHosts(String gameExternalHost, String gameInternalHost) {
        String oldInternal = _gsi.getInternalHost();
        String oldExternal = _gsi.getExternalHost();

        _gsi.setExternalHost(gameExternalHost);
        _gsi.setInternalHost(gameInternalHost);

        if (!gameExternalHost.equals("*")) {
            try {
                _gsi.setExternalIp(InetAddress.getByName(gameExternalHost).getHostAddress());
            } catch (UnknownHostException e) {
                logger.warn("Couldn't resolve hostname {}", gameExternalHost);
            }
        } else {
            _gsi.setExternalIp(ip);
        }

        if (!gameInternalHost.equals("*")) {
            try {
                _gsi.setInternalHost(InetAddress.getByName(gameInternalHost).getHostAddress());
            } catch (UnknownHostException e) {
                logger.warn("Couldn't resolve hostname {}", gameInternalHost);
            }
        } else {
            _gsi.setInternalHost(ip);
        }

        logger.info("Updated Gameserver [{}] {} IP's:", getServerId(), GameServerTable.getInstance().getServerNameById(getServerId()));
        if ((oldInternal == null) || !oldInternal.equalsIgnoreCase(gameInternalHost)) {
            logger.info("InternalIP: {}", gameInternalHost);
        }
        if ((oldExternal == null) || !oldExternal.equalsIgnoreCase(gameExternalHost)) {
            logger.info("ExternalIP: {}", gameExternalHost);
        }
    }


    private boolean isAuthed() {
        return nonNull(_gsi) && _gsi.isAuthed();
    }

    private void setGameServerInfo(GameServerInfo gsi) {
        _gsi = gsi;
    }

    private int getServerId() {
        if (nonNull(_gsi)) {
            return _gsi.getId();
        }
        return -1;
    }
}