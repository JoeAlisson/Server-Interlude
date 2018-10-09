package org.l2j.authserver.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.l2j.authserver.settings.AuthServerSettings.gameServerListenHost;
import static org.l2j.authserver.settings.AuthServerSettings.gameServerListenPort;

/**
 * @author KenM
 */
public class GameServerListener extends FloodProtectedListener {

    private static List<GameServerConnection> gameServers = new ArrayList<>();

    public GameServerListener() throws IOException {
        super(gameServerListenHost(), gameServerListenPort());
    }

    @Override
    public void addClient(Socket s) {
        GameServerConnection gst = new GameServerConnection(s);
        gameServers.add(gst);
    }

    public void removeGameServer(GameServerConnection gst) {
        gameServers.remove(gst);
    }
}
