package com.l2jbr.loginserver;

import com.l2jbr.commons.Config;
import com.l2jbr.commons.Server;
import com.l2jbr.loginserver.network.*;
import org.l2j.mmocore.ConnectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.l2jbr.loginserver.settings.LoginServerSettings.*;
import static java.util.Objects.nonNull;

/**
 * @author KenM
 */
public class AuthServer {

    private static final String LOG4J_CONFIGURATION = "log4j.configurationFile";
    private static AuthServer _instance;
    private static Logger _log;

    public static final int PROTOCOL_REV = 0x0102;

    private GameServerListener _gameServerListener;

    public AuthServer() throws Exception {
        AuthController.load();
        GameServerTable.load();

        _gameServerListener = new GameServerListener();
        _gameServerListener.start();
        _log.info("Listening for GameServers on {} : {}", gameServerListenHost(), gameServerListenPort());

        var bindAddress = loginListenHost().equals("*") ? new InetSocketAddress(loginListenPort()) :
                new InetSocketAddress(loginListenHost(), loginListenPort()) ;

        final L2LoginPacketHandler lph = new L2LoginPacketHandler();
        final SelectorHelper sh = new SelectorHelper();

        var connectionHandler = ConnectionBuilder.create(bindAddress, sh, lph, sh).threadPoolSize(2).build();
        connectionHandler.start();
        _log.info("Login Server ready on {}:{}", bindAddress.getHostString(), loginListenPort());
    }

    public static void removeGameserver(GameServerConnection gameServerConnection, String ip) {
        if (nonNull(_instance) && nonNull(_instance._gameServerListener)) {
            _instance._gameServerListener.removeGameServer(gameServerConnection);
            _instance._gameServerListener.removeFloodProtection(ip);
        }
    }

    public GameServerListener getGameServerListener() {
        return _gameServerListener;
    }

    public void shutdown(boolean restart) {
        Runtime.getRuntime().exit(restart ? 2 : 0);
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
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION);
        if (logConfigurationFile == null || logConfigurationFile.isEmpty()) {
            System.setProperty(LOG4J_CONFIGURATION, "log4j.xml");
        }
        _log = LoggerFactory.getLogger(AuthServer.class);
    }
}
