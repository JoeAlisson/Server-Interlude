package com.l2jbr.loginserver;

import com.l2jbr.commons.Config;
import com.l2jbr.commons.Server;
import com.l2jbr.loginserver.controller.AuthController;
import com.l2jbr.loginserver.network.*;
import org.l2j.mmocore.ConnectionBuilder;
import org.l2j.mmocore.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.l2jbr.commons.util.Util.isNullOrEmpty;
import static com.l2jbr.loginserver.settings.LoginServerSettings.*;
import static java.lang.Runtime.getRuntime;
import static java.util.Objects.nonNull;

public class AuthServer {

    private static final String LOG4J_CONFIGURATION = "log4j.configurationFile";
    public static final int PROTOCOL_REV = 0x0102;

    private static AuthServer _instance;
    private static Logger logger;

    private final ConnectionHandler<AuthClient> connectionHandler;
    private GameServerListener _gameServerListener;

    public AuthServer() throws Exception {
        AuthController.load();
        GameServerTable.load();

        _gameServerListener = new GameServerListener();
        _gameServerListener.start();
        logger.info("Listening for GameServers on {} : {}", gameServerListenHost(), gameServerListenPort());

        var bindAddress = loginListenHost().equals("*") ? new InetSocketAddress(loginListenPort()) :
                new InetSocketAddress(loginListenHost(), loginListenPort()) ;

        final L2LoginPacketHandler lph = new L2LoginPacketHandler();
        final SelectorHelper sh = new SelectorHelper();

        connectionHandler = ConnectionBuilder.create(bindAddress, sh, lph, sh).threadPoolSize(2).build();
        connectionHandler.start();
        logger.info("Login Server ready on {}:{}", bindAddress.getHostString(), loginListenPort());
    }

    public void removeGameserver(GameServerConnection gameServerConnection, String ip) {
        if (nonNull(_gameServerListener)) {
            _gameServerListener.removeGameServer(gameServerConnection);
            _gameServerListener.removeFloodProtection(ip);
        }
    }

    public void shutdown(boolean restart) {
        connectionHandler.shutdown();
        connectionHandler.setDaemon(true);
        getRuntime().exit(restart ? 2 : 0);
    }

    public static AuthServer getInstance() {
        return _instance;
    }

    public static void main(String[] args) {
        configureLogger();
        Server.serverMode = Server.MODE_LOGINSERVER;
        Config.load();
        try {
            _instance = new AuthServer();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION);
        if (isNullOrEmpty(logConfigurationFile)) {
            System.setProperty(LOG4J_CONFIGURATION, "log4j.xml");
        }
        logger = LoggerFactory.getLogger(AuthServer.class);
    }
}
