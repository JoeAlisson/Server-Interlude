package org.l2j.authserver.network;

import org.l2j.commons.crypt.NewCrypt;
import org.l2j.authserver.AuthServer;
import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.GameServerManager;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.packet.game2auth.*;
import org.l2j.authserver.network.packet.auth2game.*;
import org.l2j.authserver.network.packet.ServerBasePacket;
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

import static org.l2j.commons.util.Util.printData;
import static java.lang.Byte.toUnsignedInt;
import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.packet.auth2game.LoginServerFail.NOT_AUTHED;
import static org.l2j.authserver.settings.AuthServerSettings.acceptNewGameServerEnabled;

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

    public GameServerConnection(Socket con) {
        socket = con;
        ip = con.getInetAddress().getHostAddress();
        try {
            _in = socket.getInputStream();
            _out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        KeyPair pair = GameServerManager.getInstance().getKeyPair();
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
                serverName = String.format("[%d] %s", getServerId(), GameServerManager.getInstance().getServerNameById(getServerId()));
            }

            String msg = String.format("GameServer %s : Connection Lost: %s", serverName , e.getLocalizedMessage());
            logger.info(msg);
        } finally {
            if (isAuthed()) {
                _gsi.setDown();
                logger.info("Server [{}] {} is now set as disconnect", getServerId(), GameServerManager.getInstance().getServerNameById(getServerId()));
            }
            AuthServer.getInstance().removeGameserver(this, ip);
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
            case 0x07:
                onReceiveAccountInfo(data);
                break;
            default:
                logger.warn("Unknown Opcode ({}) from GameServer, closing socket.", Integer.toHexString(packetType).toUpperCase());
                forceClose(NOT_AUTHED);
        }
    }

    private void onReceiveAccountInfo(byte[] data) {
        AccountInfo info = new AccountInfo(data);
        AuthController.getInstance().addAccountCharactersInfo(getServerId(), info.getAccount(), info.getPlayers());
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
        }
    }

    private void onReceivePlayerInGame(byte[] data) {
        if (isAuthed()) {
            PlayerInGame pig = new PlayerInGame(data);
            List<String> newAccounts = pig.getAccounts();
            for (String account : newAccounts) {
                _gsi.addAccount(account);
                logger.debug("Account {} logged in GameServer: [{}] {}",  account, getServerId(), GameServerManager.getInstance().getServerNameById(getServerId()));
            }
        } else {
            forceClose(NOT_AUTHED);
        }
    }

    private void onReceivePlayerLogOut(byte[] data) {
        if (isAuthed()) {
            PlayerLogout plo = new PlayerLogout(data);
            _gsi.removeAccount(plo.getAccount());
            logger.debug("Player {} logged out from gameserver [{}] {}", plo.getAccount(), getServerId(), GameServerManager.getInstance().getServerNameById(getServerId()));
        } else {
            forceClose(NOT_AUTHED);
        }
    }

    private void onReceiveChangeAccessLevel(byte[] data) {
        if (isAuthed()) {
            ChangeAccessLevel cal = new ChangeAccessLevel(data);
            AuthController.getInstance().setAccountAccessLevel(cal.getAccount(), (short) cal.getLevel());
            logger.info("Changed {} access level to {}", cal.getAccount(), cal.getLevel());
        } else {
            forceClose(NOT_AUTHED);
        }
    }

    private void onReceivePlayerAuthRequest(byte[] data) throws IOException {
        if (isAuthed()) {
            PlayerAuthRequest par = new PlayerAuthRequest(data);

            logger.debug("auth request received for Player {}", par.getAccount());

            SessionKey key = AuthController.getInstance().getKeyForAccount(par.getAccount());

            PlayerAuthResponse authResponse;
            if (Objects.equals(par.getKey(), key)) {
                AuthController.getInstance().removeAuthedClient(par.getAccount());
                authResponse = new PlayerAuthResponse(par.getAccount(), 1);
            } else {
                logger.debug("auth request: NO");
                logger.debug("session key from self: {}", key);
                logger.debug("session key sent: {}", par.getKey());

                authResponse = new PlayerAuthResponse(par.getAccount(), 0);
            }
            sendPacket(authResponse);
        } else {
            forceClose(NOT_AUTHED);
        }
    }

    private void onReceiveServerStatus(byte[] data) {
        if (isAuthed()) {
            logger.debug("ServerStatus received");

            new ServerStatus(data, getServerId());
        } else {
            forceClose(NOT_AUTHED);
        }
    }

    private void handleAuthProcess(GameServerAuth gameServerAuth) {
        GameServerManager gameServerManager = GameServerManager.getInstance();

        int id = gameServerAuth.getDesiredID();
        byte[] hexId = gameServerAuth.getHexID();

        GameServerInfo gsi = gameServerManager.getRegisteredGameServerById(id);
        if (nonNull(gsi)) {
            if (Arrays.equals(gsi.getHexId(), hexId)) {
                if (gsi.isAuthed()) {
                    forceClose(LoginServerFail.REASON_ALREADY_LOGGED);
                } else {
                    attachGameServerInfo(gsi, gameServerAuth);
                }
            } else {
                // there is already a server registered with the desired id and different hex id
                // try to registerClient this one with an alternative id
                if (acceptNewGameServerEnabled() && gameServerAuth.acceptAlternateID()) {
                    gsi = new GameServerInfo(id, hexId, this);
                    if (gameServerManager.registerWithFirstAvaliableId(gsi)) {
                        attachGameServerInfo(gsi, gameServerAuth);
                        gameServerManager.registerServerOnDB(gsi);
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
                if (gameServerManager.register(id, gsi)) {
                    attachGameServerInfo(gsi, gameServerAuth);
                    gameServerManager.registerServerOnDB(gsi);
                } else {
                    forceClose(LoginServerFail.REASON_ID_RESERVED);
                }
            } else {
                forceClose(LoginServerFail.REASON_WRONG_HEXID);
            }
        }
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
        gsi.setServerType(gameServerAuth.getServerType());
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

    public void kickPlayer(String account) {
        KickPlayer kp = new KickPlayer(account);
        try {
            sendPacket(kp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestAccountInfo(String account) {
        RequestAccountInfo packet = new RequestAccountInfo(account);
        try {
            sendPacket(packet);
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

        logger.info("Updated Gameserver [{}] {} IP's:", getServerId(), GameServerManager.getInstance().getServerNameById(getServerId()));
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