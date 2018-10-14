/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jbr.gameserver;

import com.l2jbr.commons.Config;
import com.l2jbr.commons.crypt.NewCrypt;
import com.l2jbr.commons.util.Rnd;
import com.l2jbr.commons.util.Util;
import com.l2jbr.gameserver.gameserverpackets.*;
import com.l2jbr.gameserver.loginserverpackets.*;
import com.l2jbr.gameserver.model.L2World;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.database.repository.CharacterRepository;
import com.l2jbr.gameserver.network.L2GameClient;
import com.l2jbr.gameserver.serverpackets.CharSelectInfo;
import com.l2jbr.gameserver.serverpackets.LoginResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jbr.commons.database.DatabaseAccess.getRepository;
import static com.l2jbr.gameserver.network.L2GameClient.GameClientState.AUTHED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


public class LoginServerThread extends Thread {
    protected static final Logger _log = LoggerFactory.getLogger(LoginServerThread.class.getName());

    private static LoginServerThread _instance;

    private static final int REVISION = 0x0102;
    private final int _serverType;
    private RSAPublicKey _publicKey;
    private final String _hostname;
    private final int _port;
    private final int _gamePort;
    private Socket _loginSocket;
    private InputStream _in;
    private OutputStream _out;

    /**
     * The BlowFish engine used to encrypt packets<br>
     * It is first initialized with a unified key:<br>
     * "_;v.]05-31!|+-%xT!^[$\00"<br>
     * <br>
     * and then after handshake, with a new key sent by<br>
     * loginserver during the handshake. This new key is stored<br>
     * in {@link #_blowfishKey}
     */
    private NewCrypt _blowfish;
    private byte[] _blowfishKey;
    private byte[] _hexID;
    private final boolean _acceptAlternate;
    private int _requestID;
    private int _serverID;
    private final boolean _reserveHost;
    private int _maxPlayer;
    // TODO use Map
    private final List<WaitingClient> _waitingClients;
    private final Map<String, L2GameClient> _accountsInGameServer;
    private int _status;
    private String _serverName;
    private final String _gameExternalHost;
    private final String _gameInternalHost;

    private LoginServerThread() {
        super("LoginServerThread");
        _port = Config.GAME_SERVER_LOGIN_PORT;
        _gamePort = Config.PORT_GAME;
        _hostname = Config.GAME_SERVER_LOGIN_HOST;
        _hexID = Config.HEX_ID;
        if (isNull(_hexID)) {
            _requestID = Config.REQUEST_ID;
            _hexID = generateHex(16);
        } else {
            _requestID = Config.SERVER_ID;
        }
        _acceptAlternate = Config.ACCEPT_ALTERNATE_ID;
        _reserveHost = Config.RESERVE_HOST_ON_LOGIN;
        _gameExternalHost = Config.EXTERNAL_HOSTNAME;
        _gameInternalHost = Config.INTERNAL_HOSTNAME;
        _waitingClients = new LinkedList<>();
        _accountsInGameServer = new ConcurrentHashMap<>();
        _maxPlayer = Config.MAXIMUM_ONLINE_USERS;
        _serverType = Config.SERVER_TYPE;
    }

    public static LoginServerThread getInstance() {
        if (isNull(_instance)) {
            _instance = new LoginServerThread();
        }
        return _instance;
    }

    @Override
    public void run() {
        while (true) {
            int lengthHi = 0;
            int lengthLo = 0;
            int length = 0;
            boolean checksumOk = false;
            try {
                // Connection
                _log.info("Connecting to login on " + _hostname + ":" + _port);
                _loginSocket = new Socket(_hostname, _port);
                _in = _loginSocket.getInputStream();
                _out = new BufferedOutputStream(_loginSocket.getOutputStream());

                // init Blowfish
                _blowfishKey = generateHex(40);
                _blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
                while (true) {
                    lengthLo = _in.read();
                    lengthHi = _in.read();
                    length = (lengthHi * 256) + lengthLo;

                    if (lengthHi < 0) {
                        _log.debug("LoginServerThread: Login terminated the connection.");
                        break;
                    }

                    byte[] incoming = new byte[length];
                    incoming[0] = (byte) lengthLo;
                    incoming[1] = (byte) lengthHi;

                    int receivedBytes = 0;
                    int newBytes = 0;
                    while ((newBytes != -1) && (receivedBytes < (length - 2))) {
                        newBytes = _in.read(incoming, 2, length - 2);
                        receivedBytes = receivedBytes + newBytes;
                    }

                    if (receivedBytes != (length - 2)) {
                        _log.warn("Incomplete Packet is sent to the server, closing connection.(LS)");
                        break;
                    }

                    byte[] decrypt = new byte[length - 2];
                    System.arraycopy(incoming, 2, decrypt, 0, decrypt.length);
                    // decrypt if we have a key
                    decrypt = _blowfish.decrypt(decrypt);
                    checksumOk = NewCrypt.verifyChecksum(decrypt);

                    if (!checksumOk) {
                        _log.warn("Incorrect packet checksum, ignoring packet (LS)");
                        break;
                    }

                    if (Config.DEBUG) {
                        _log.warn("[C]\n" + Util.printData(decrypt));
                    }

                    int packetType = decrypt[0] & 0xff;
                    switch (packetType) {
                        case 0x00:
                            InitLS init = new InitLS(decrypt);
                            if (Config.DEBUG) {
                                _log.info("Init received");
                            }
                            if (init.getRevision() != REVISION) {
                                // TODO: revision mismatch
                                _log.warn("/!\\ Revision mismatch between LS and GS /!\\");
                                break;
                            }
                            try {
                                KeyFactory kfac = KeyFactory.getInstance("RSA");
                                BigInteger modulus = new BigInteger(init.getRSAKey());
                                RSAPublicKeySpec kspec1 = new RSAPublicKeySpec(modulus, RSAKeyGenParameterSpec.F4);
                                _publicKey = (RSAPublicKey) kfac.generatePublic(kspec1);
                                if (Config.DEBUG) {
                                    _log.info("RSA key set up");
                                }
                            } catch (GeneralSecurityException e) {
                                _log.warn("Troubles while init the public key send by login");
                                break;
                            }
                            // send the blowfish key through the rsa encryption
                            BlowFishKey bfk = new BlowFishKey(_blowfishKey, _publicKey);
                            sendPacket(bfk);
                            if (Config.DEBUG) {
                                _log.info("Sent new blowfish key");
                            }
                            // now, only accept paket with the new encryption
                            _blowfish = new NewCrypt(_blowfishKey);
                            if (Config.DEBUG) {
                                _log.info("Changed blowfish key");
                            }
                            AuthRequest ar = new AuthRequest(_requestID, _acceptAlternate, _hexID, _gameExternalHost, _gameInternalHost, _gamePort, _reserveHost, _maxPlayer, _serverType);
                            sendPacket(ar);
                            if (Config.DEBUG) {
                                _log.info("Sent AuthRequest to login");
                            }
                            break;
                        case 0x01:
                            LoginServerFail lsf = new LoginServerFail(decrypt);
                            _log.info("Damn! Registeration Failed: " + lsf.getReasonString());
                            // login will close the connection here
                            break;
                        case 0x02:
                            AuthResponse aresp = new AuthResponse(decrypt);
                            _serverID = aresp.getServerId();
                            _serverName = aresp.getServerName();
                            Config.saveHexid(_serverID, hexToString(_hexID));
                            _log.info("Registered on login as Server " + _serverID + " : " + _serverName);
                            ServerStatus st = new ServerStatus();
                            if (Config.SERVER_LIST_BRACKET) {
                                st.addAttribute(ServerStatus.SERVER_LIST_SQUARE_BRACKET, ServerStatus.ON);
                            } else {
                                st.addAttribute(ServerStatus.SERVER_LIST_SQUARE_BRACKET, ServerStatus.OFF);
                            }
                            if (Config.SERVER_LIST_CLOCK) {
                                st.addAttribute(ServerStatus.SERVER_LIST_CLOCK, ServerStatus.ON);
                            } else {
                                st.addAttribute(ServerStatus.SERVER_LIST_CLOCK, ServerStatus.OFF);
                            }
                            if (Config.SERVER_LIST_TESTSERVER) {
                                st.addAttribute(ServerStatus.TEST_SERVER, ServerStatus.ON);
                            } else {
                                st.addAttribute(ServerStatus.TEST_SERVER, ServerStatus.OFF);
                            }
                            if (Config.SERVER_GMONLY) {
                                st.addAttribute(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GM_ONLY);
                            } else {
                                st.addAttribute(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_AUTO);
                            }
                            sendPacket(st);
                            if (L2World.getInstance().getAllPlayersCount() > 0) {
                                List<String> playerList = new LinkedList<>();
                                for (L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                                    playerList.add(player.getAccountName());
                                }
                                PlayerInGame pig = new PlayerInGame(playerList);
                                sendPacket(pig);
                            }
                            break;
                        case 0x03:
                            PlayerAuthResponse par = new PlayerAuthResponse(decrypt);
                            String account = par.getAccount();
                            WaitingClient wcToRemove = null;
                            synchronized (_waitingClients) {
                                for (WaitingClient wc : _waitingClients) {
                                    if (wc.account.equals(account)) {
                                        wcToRemove = wc;
                                    }
                                }
                            }
                            if (nonNull(wcToRemove)) {
                                if (par.isAuthed()) {
                                    _log.debug("Login accepted player {} waited({} ms)", wcToRemove.account, (GameTimeController.getGameTicks() - wcToRemove.timestamp));

                                    wcToRemove.gameClient.setState(AUTHED);
                                    wcToRemove.gameClient.sendPacket(LoginResult.SUCCESS);
                                    PlayerInGame pig = new PlayerInGame(par.getAccount());
                                    sendPacket(pig);

                                    wcToRemove.gameClient.setSessionId(wcToRemove.session);
                                    CharSelectInfo cl = new CharSelectInfo(wcToRemove.account, wcToRemove.gameClient.getSessionId().sessionId);
                                    wcToRemove.gameClient.sendPacket(cl);
                                    wcToRemove.gameClient.setCharSelection(cl.getCharInfo());
                                } else {
                                    _log.warn("Auth server disconnected. closing connection");
                                    wcToRemove.gameClient.close(LoginResult.ACCESS_FAILED_TRY_LATER);
                                    removeServerLogin(account);
                                }
                                _waitingClients.remove(wcToRemove);
                            }
                            break;
                        case 0x04:
                            KickPlayer kp = new KickPlayer(decrypt);
                            doKickPlayer(kp.getAccount());
                            break;
                        case 0x05:
                            RequestAccountInfo accountInfo = new RequestAccountInfo(decrypt);
                            responseAccountInfo(accountInfo.getAccount());
                            break;
                    }
                }
            } catch (UnknownHostException e) {
                if (Config.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                _log.info("Deconnected from Login, Trying to reconnect:");
                _log.info(e.toString());
            } finally {
                try {
                    _loginSocket.close();
                } catch (Exception e) {
                }
            }

            try {
                Thread.sleep(5000); // 5 seconds tempo.
            } catch (InterruptedException e) {
                //
            }
        }
    }

    private void responseAccountInfo(String account) {
        var playersInAccount = getRepository(CharacterRepository.class).countByAccount(account);
        AccountInfo info = new AccountInfo(account, playersInAccount);
        try {
            sendPacket(info);
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
        }

    }

    public void addWaitingClientAndSendRequest(String acc, L2GameClient client, SessionKey key) {
        WaitingClient wc = new WaitingClient(acc, client, key);
        synchronized (_waitingClients) {
            _waitingClients.add(wc);
        }
        PlayerAuthRequest par = new PlayerAuthRequest(acc, key);
        try {
            sendPacket(par);
        } catch (IOException e) {
            _log.warn("Error while sending player auth request");
            if (Config.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void removeWaitingClient(L2GameClient client) {
        WaitingClient toRemove = null;
        synchronized (_waitingClients) {
            for (WaitingClient c : _waitingClients) {
                if (c.gameClient == client) {
                    toRemove = c;
                }
            }
            if (toRemove != null) {
                _waitingClients.remove(toRemove);
            }
        }
    }

    public void sendLogout(String account) {
        removeServerLogin(account);
        PlayerLogout pl = new PlayerLogout(account);
        try {
            sendPacket(pl);
        } catch (IOException e) {
            _log.warn("Error while sending logout packet to login");
            if (Config.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public L2GameClient addGameServerLogin(String account, L2GameClient client) {
        return _accountsInGameServer.putIfAbsent(account, client);
    }

    public void removeServerLogin(String account) {
        _accountsInGameServer.remove(account);
    }

    public void sendAccessLevel(String account, int level) {
        ChangeAccessLevel cal = new ChangeAccessLevel(account, level);
        try {
            sendPacket(cal);
        } catch (IOException e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private String hexToString(byte[] hex) {
        return new BigInteger(hex).toString(16);
    }

    public void doKickPlayer(String account) {
        final L2GameClient client = _accountsInGameServer.get(account);
        if (client != null) {
            client.closeNow();
            LoginServerThread.getInstance().sendLogout(account);
        }
    }

    public static byte[] generateHex(int size) {
        byte[] array = new byte[size];
        Rnd.nextBytes(array);
        if (Config.DEBUG) {
            _log.debug("Generated random String:  \"" + array + "\"");
        }
        return array;
    }

    /**
     * @param sl
     * @throws IOException
     */
    private void sendPacket(GameServerBasePacket sl) throws IOException {
        byte[] data = sl.getContent();
        NewCrypt.appendChecksum(data);
        if (Config.DEBUG) {
            _log.debug("[S]\n" + Util.printData(data));
        }
        data = _blowfish.crypt(data);

        int len = data.length + 2;
        synchronized (_out) // avoids tow threads writing in the mean time
        {
            _out.write(len & 0xff);
            _out.write((len >> 8) & 0xff);
            _out.write(data);
            _out.flush();
        }
    }

    /**
     * @param maxPlayer The maxPlayer to set.
     */
    public void setMaxPlayer(int maxPlayer) {
        sendServerStatus(ServerStatus.MAX_PLAYERS, maxPlayer);
        _maxPlayer = maxPlayer;
    }

    /**
     * @return Returns the maxPlayer.
     */
    public int getMaxPlayer() {
        return _maxPlayer;
    }

    /**
     * @param id
     * @param value
     */
    public void sendServerStatus(int id, int value) {
        ServerStatus ss = new ServerStatus();
        ss.addAttribute(id, value);
        try {
            sendPacket(ss);
        } catch (IOException e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    public String getStatusString() {
        return ServerStatus.STATUS_STRING[_status];
    }

    /**
     * @return
     */
    public boolean isClockShown() {
        return Config.SERVER_LIST_CLOCK;
    }

    /**
     * @return
     */
    public boolean isBracketShown() {
        return Config.SERVER_LIST_BRACKET;
    }

    /**
     * @return Returns the serverName.
     */
    public String getServerName() {
        return _serverName;
    }

    public void setServerStatus(int status) {
        switch (status) {
            case ServerStatus.STATUS_AUTO:
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_AUTO);
                _status = status;
                break;
            case ServerStatus.STATUS_DOWN:
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_DOWN);
                _status = status;
                break;
            case ServerStatus.STATUS_FULL:
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_FULL);
                _status = status;
                break;
            case ServerStatus.STATUS_GM_ONLY:
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GM_ONLY);
                _status = status;
                break;
            case ServerStatus.STATUS_GOOD:
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GOOD);
                _status = status;
                break;
            case ServerStatus.STATUS_NORMAL:
                sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_NORMAL);
                _status = status;
                break;
            default:
                throw new IllegalArgumentException("Status does not exists:" + status);
        }
    }



    public static class SessionKey {
        public int sessionId;
        public int accountId;
        public int authAccountId;
        public int authKey;

        public SessionKey(int authAccountId, int authKey, int sessionId, int accountId) {
            this.sessionId = sessionId;
            this.accountId = accountId;
            this.authAccountId = authAccountId;
            this.authKey = authKey;
        }

        @Override
        public String toString() {
            return "server Keys: " + sessionId + " " + accountId + " auth Keys:" + authAccountId + " " + authKey;
        }
    }

    private class WaitingClient {
        public int timestamp;
        public String account;
        public L2GameClient gameClient;
        public SessionKey session;

        public WaitingClient(String acc, L2GameClient client, SessionKey key) {
            account = acc;
            timestamp = GameTimeController.getGameTicks();
            gameClient = client;
            session = key;
        }
    }
}