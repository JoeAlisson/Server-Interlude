package com.l2jbr.loginserver.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static com.l2jbr.loginserver.settings.LoginServerSettings.gameServerListenHost;
import static com.l2jbr.loginserver.settings.LoginServerSettings.gameServerListenPort;

/**
 * @author KenM
 */
public class GameServerListener extends FloodProtectedListener {
    private static Logger logger = LoggerFactory.getLogger(GameServerListener.class.getName());
    private static List<GameServerConnection> gameServers = new LinkedList<>();

    public GameServerListener() throws IOException {
        super(gameServerListenHost(), gameServerListenPort());
    }

    @Override
    public void addClient(Socket s) {
        logger.debug("Received gameserver connection from: {}", s.getInetAddress().getHostAddress());
        GameServerConnection gst = new GameServerConnection(s);
        gameServers.add(gst);
    }

    public void removeGameServer(GameServerConnection gst) {
        gameServers.remove(gst);
    }
}
