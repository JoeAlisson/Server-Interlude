/* This program is free software; you can redistribute it and/or modify
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
package com.l2jbr.loginserver;

import com.l2jbr.commons.database.GameserverRepository;
import com.l2jbr.commons.database.model.GameServer;
import com.l2jbr.commons.util.Rnd;
import com.l2jbr.commons.xml.XMLDocumentFactory;
import com.l2jbr.loginserver.network.GameServerConnection;
import com.l2jbr.loginserver.network.gameserverpackets.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jbr.commons.database.DatabaseAccess.getRepository;
import static java.util.Objects.isNull;


/**
 * @author KenM
 */
public class GameServerTable {
    private static Logger _log = LoggerFactory.getLogger(GameServerTable.class.getName());
    private static GameServerTable _instance;

    // Server Names Config
    private static Map<Integer, String> _serverNames = new LinkedHashMap<>();

    // Game Server Table
    private final Map<Integer, GameServerInfo> _gameServerTable = new ConcurrentHashMap<>();

    // RSA Config
    private static final int KEYS_SIZE = 10;
    private KeyPair[] _keyPairs;

    public static void load() throws SQLException, GeneralSecurityException {
        if (_instance == null) {
            _instance = new GameServerTable();
        } else {
            throw new IllegalStateException("Load can only be invoked a single time.");
        }
    }

    public static GameServerTable getInstance() {
        return _instance;
    }

    public GameServerTable() throws SQLException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        loadServerNames();
        _log.info("Loaded " + _serverNames.size() + " server names");

        loadRegisteredGameServers();
        _log.info("Loaded " + _gameServerTable.size() + " registered Game Servers");

        loadRSAKeys();
        _log.info("Cached " + _keyPairs.length + " RSA keys for Game Server communication.");
    }

    private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
        keyGen.initialize(spec);

        _keyPairs = new KeyPair[KEYS_SIZE];
        for (int i = 0; i < KEYS_SIZE; i++) {
            _keyPairs[i] = keyGen.genKeyPair();
        }
    }

    private void loadServerNames() {
        try {
            File f = new File("servername.xml");
            Document doc = XMLDocumentFactory.getInstance().loadDocument(f);

            Node n = doc.getFirstChild();
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                if (d.getNodeName().equalsIgnoreCase("server")) {
                    NamedNodeMap attrs = d.getAttributes();

                    int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                    String name = attrs.getNamedItem("name").getNodeValue();

                    _serverNames.put(id, name);
                }
            }
        } catch (Exception e) {
            _log.warn("GameServerTable: servername.xml could not be loaded.");
        }

    }

    private void loadRegisteredGameServers() {
        GameserverRepository repository = getRepository(GameserverRepository.class);
        repository.findAll().forEach(gameServer -> {
            int id = gameServer.getId();
            GameServerInfo gsi = new GameServerInfo(id, stringToHex(gameServer.getHexid()));
            _gameServerTable.put(id, gsi);
        });
    }

    public Map<Integer, GameServerInfo> getRegisteredGameServers() {
        return _gameServerTable;
    }

    public GameServerInfo getRegisteredGameServerById(int id) {
        return _gameServerTable.get(id);
    }

    public boolean hasRegisteredGameServerOnId(int id) {
        return _gameServerTable.containsKey(id);
    }

    public boolean registerWithFirstAvaliableId(GameServerInfo gsi) {
        // avoid two servers registering with the same "free" id
        synchronized (_gameServerTable) {
            for (Entry<Integer, String> entry : _serverNames.entrySet()) {
                if (!_gameServerTable.containsKey(entry.getKey())) {
                    _gameServerTable.put(entry.getKey(), gsi);
                    gsi.setId(entry.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean register(int id, GameServerInfo gsi) {
        if(isNull(_gameServerTable.putIfAbsent(id, gsi))) {
            gsi.setId(id);
            return true;
        }
        return false;
    }

    public void registerServerOnDB(GameServerInfo gsi) {
        registerServerOnDB(gsi.getHexId(), gsi.getId(), gsi.getExternalHost());
    }

    public void registerServerOnDB(byte[] hexId, int id, String externalHost) {
        GameServer gameServer = new GameServer(id, hexToString(hexId), externalHost);
        getRepository(GameserverRepository.class).save(gameServer);
    }

    public String getServerNameById(int id) {
        return getServerNames().get(id);
    }

    public Map<Integer, String> getServerNames() {
        return _serverNames;
    }

    public KeyPair getKeyPair() {
        return _keyPairs[Rnd.nextInt(10)];
    }

    private byte[] stringToHex(String string) {
        return new BigInteger(string, 16).toByteArray();
    }

    private String hexToString(byte[] hex) {
        if (hex == null) {
            return "null";
        }
        return new BigInteger(hex).toString(16);
    }

    public static class GameServerInfo {
        // auth
        private int _id;
        private final byte[] _hexId;
        private volatile boolean _isAuthed;

        // status
        private GameServerConnection _gst;
        private int _status;

        // network
        private String internalIp;
        private String externalIp;
        private String externalHost;
        private int _port;

        // config
        private final boolean _isPvp = true;
        private boolean _isTestServer;
        private boolean _isShowingClock;
        private boolean _isShowingBrackets;
        private int _maxPlayers;

        public GameServerInfo(int id, byte[] hexId, GameServerConnection gst) {
            _id = id;
            _hexId = hexId;
            _gst = gst;
            _status = ServerStatus.STATUS_DOWN;
        }

        public GameServerInfo(int id, byte[] hexId) {
            this(id, hexId, null);
        }

        public void setId(int id) {
            _id = id;
        }

        public int getId() {
            return _id;
        }

        public byte[] getHexId() {
            return _hexId;
        }

        public void setAuthed(boolean isAuthed) {
            _isAuthed = isAuthed;
        }

        public boolean isAuthed() {
            return _isAuthed;
        }

        public void setGameServerThread(GameServerConnection gst) {
            _gst = gst;
        }

        public GameServerConnection getGameServerThread() {
            return _gst;
        }

        public void setStatus(int status) {
            _status = status;
        }

        public int getStatus() {
            return _status;
        }

        public int getOnlinePlayersCount() {
            if (_gst == null) {
                return 0;
            }
            return _gst.getPlayerCount();
        }

        public void setInternalHost(String internalIp) {
            this.internalIp = internalIp;
        }

        public String getInternalHost() {
            return internalIp;
        }

        public void setExternalIp(String externalIp) {
            this.externalIp = externalIp;
        }

        public String getExternalIp() {
            return externalIp;
        }

        public void setExternalHost(String externalHost) {
            this.externalHost = externalHost;
        }

        public String getExternalHost() {
            return externalHost;
        }

        public int getPort() {
            return _port;
        }

        public void setPort(int port) {
            _port = port;
        }

        public void setMaxPlayers(int maxPlayers) {
            _maxPlayers = maxPlayers;
        }

        public int getMaxPlayers() {
            return _maxPlayers;
        }

        public boolean isPvp() {
            return _isPvp;
        }

        public void setTestServer(boolean val) {
            _isTestServer = val;
        }

        public boolean isTestServer() {
            return _isTestServer;
        }

        public void setShowingClock(boolean clock) {
            _isShowingClock = clock;
        }

        public boolean isShowingClock() {
            return _isShowingClock;
        }

        public void setShowingBrackets(boolean val) {
            _isShowingBrackets = val;
        }

        public boolean isShowingBrackets() {
            return _isShowingBrackets;
        }

        public void setDown() {
            setAuthed(false);
            setPort(0);
            setGameServerThread(null);
            setStatus(ServerStatus.STATUS_DOWN);
        }
    }
}
