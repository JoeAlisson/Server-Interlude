package org.l2j.authserver;

import com.l2jbr.commons.database.model.GameServer;
import org.l2j.authserver.network.GameServerConnection;
import org.l2j.authserver.network.packet.game2auth.ServerStatus;

import static com.l2jbr.commons.util.Util.stringToHex;

public class GameServerInfo {

    private int _id;
    private final byte[] _hexId;
    private volatile boolean _isAuthed;

    private GameServerConnection connection;
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

    public GameServerInfo(GameServer gameServer) {
        this(gameServer.getId(), stringToHex(gameServer.getHexid()), null);
    }

    public GameServerInfo(int id, byte[] hexId, GameServerConnection serverConnection) {
        _id = id;
        _hexId = hexId;
        connection = serverConnection;
        _status = ServerStatus.STATUS_DOWN;
    }

    public int getOnlinePlayersCount() {
        if (connection == null) {
            return 0;
        }
        return connection.getPlayerCount();
    }

    public void setDown() {
        setAuthed(false);
        setPort(0);
        setGameServerThread(null);
        setStatus(ServerStatus.STATUS_DOWN);
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
        connection = gst;
    }

    public GameServerConnection getGameServerThread() {
        return connection;
    }

    public void setStatus(int status) {
        _status = status;
    }

    public int getStatus() {
        return _status;
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
}