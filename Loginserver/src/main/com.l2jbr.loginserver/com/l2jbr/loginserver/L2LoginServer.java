package com.l2jbr.loginserver;

import com.l2jbr.commons.Config;
import com.l2jbr.commons.Server;
import com.l2jbr.commons.status.Status;
import com.l2jbr.loginserver.network.*;
import com.l2jbr.loginserver.status.LoginStatus;
import org.l2j.mmocore.ConnectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import static java.util.Objects.nonNull;

/**
 * @author KenM
 */
public class L2LoginServer {

    public static final int PROTOCOL_REV = 0x0102;
    private static final String LOG4J_CONFIGURATION = "log4j.configurationFile";

    private static L2LoginServer _instance;
    private static Logger _log;
    private GameServerListener _gameServerListener;
    private Status _statusServer;

    public static void main(String[] args) {
        configureLogger();
        _instance = new L2LoginServer();
    }

    public static L2LoginServer getInstance() {
        return _instance;
    }

    public L2LoginServer() {
        Server.serverMode = Server.MODE_LOGINSERVER;
        Config.load();

        try {
            LoginController.load();
        } catch (GeneralSecurityException e) {
            _log.error("FATAL: Failed initializing LoginController. Reason: {}", e.getMessage());

            if (Config.DEVELOPER) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        try {
            GameServerTable.load();
        } catch (GeneralSecurityException | SQLException e) {
            _log.error("FATAL: Failed to load GameServerTable. Reason: {}", e.getMessage());
            if (Config.DEVELOPER) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        loadBanFile();

        try {
            _gameServerListener = new GameServerListener();
            _gameServerListener.start();
            _log.info("Listening for GameServers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
        } catch (IOException e) {
            _log.error("FATAL: Failed to start the Game Server Listener. Reason: " + e.getMessage());
            if (Config.DEVELOPER) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        if (Config.IS_TELNET_ENABLED) {
            try {
                _statusServer = new LoginStatus();
                _statusServer.start();
            } catch (IOException e) {
                _log.error("Failed to start the Telnet Server. Reason: " + e.getMessage());
                if (Config.DEVELOPER) {
                    e.printStackTrace();
                }
            }
        } else {
            _log.info("Telnet server is currently disabled.");
        }

        InetSocketAddress bindAddress;
        if (!Config.LOGIN_BIND_ADDRESS.equals("*")) {
            bindAddress =  new InetSocketAddress(Config.LOGIN_BIND_ADDRESS, Config.PORT_LOGIN);
        } else {
            bindAddress = new InetSocketAddress(Config.PORT_LOGIN);
        }

        try {
            final L2LoginPacketHandler lph = new L2LoginPacketHandler();
            final SelectorHelper sh = new SelectorHelper();

            var connectionHandler = ConnectionBuilder.create(bindAddress, sh,lph,sh).threadPoolSize(2).build();
            connectionHandler.start();
        } catch (IOException e) {
            _log.error("FATAL: Failed to open ConnectionHandler. Reason: " + e.getMessage(), e);
            System.exit(1);
        }
        _log.info("Login Server ready on {}:{}", bindAddress.getHostString(),  Config.PORT_LOGIN);
    }

    public static void sendMessageToStatusServer(String msg) {
        if(nonNull(_instance) &&  nonNull(_instance._statusServer)) {
            _instance._statusServer.sendMessageToTelnets(msg);
        }
    }

    public static void removeGameserver(GameServerConnection gameServerConnection, String ip) {
        if(nonNull(_instance) && nonNull(_instance._gameServerListener)) {
            _instance._gameServerListener.removeGameServer(gameServerConnection);
            _instance._gameServerListener.removeFloodProtection(ip);
        }
    }

    public Status getStatusServer() {
        return _statusServer;
    }

    public GameServerListener getGameServerListener() {
        return _gameServerListener;
    }

    private void loadBanFile() {
        File bannedFile = new File("./banned_ip.cfg");
        if (bannedFile.exists() && bannedFile.isFile()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(bannedFile);
            } catch (FileNotFoundException e) {
                _log.warn("Failed to load banned IPs file (" + bannedFile.getName() + ") for reading. Reason: " + e.getMessage());
                if (Config.DEVELOPER) {
                    e.printStackTrace();
                }
                return;
            }

            LineNumberReader reader = new LineNumberReader(new InputStreamReader(fis));

            String line;
            String[] parts;
            try {

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // check if this line isn't a comment line
                    if ((line.length() > 0) && (line.charAt(0) != '#')) {
                        // split comments if any
                        parts = line.split("#");

                        // discard comments in the line, if any
                        line = parts[0];

                        parts = line.split(" ");

                        String address = parts[0];

                        long duration = 0;

                        if (parts.length > 1) {
                            try {
                                duration = Long.parseLong(parts[1]);
                            } catch (NumberFormatException e) {
                                _log.warn("Skipped: Incorrect ban duration (" + parts[1] + ") on (" + bannedFile.getName() + "). Line: " + reader.getLineNumber());
                                continue;
                            }
                        }

                        try {
                            LoginController.getInstance().addBanForAddress(address, duration);
                        } catch (UnknownHostException e) {
                            _log.warn("Skipped: Invalid address (" + parts[0] + ") on (" + bannedFile.getName() + "). Line: " + reader.getLineNumber());
                        }
                    }
                }
            } catch (IOException e) {
                _log.warn("Error while reading the bans file (" + bannedFile.getName() + "). Details: " + e.getMessage());
                if (Config.DEVELOPER) {
                    e.printStackTrace();
                }
            }
            _log.info("Loaded " + LoginController.getInstance().getBannedIps().size() + " IP Bans.");
        } else {
            _log.info("IP Bans file (" + bannedFile.getName() + ") is missing or is a directory, skipped.");
        }
    }

    public void shutdown(boolean restart) {
        Runtime.getRuntime().exit(restart ? 2 : 0);
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION);
        if (logConfigurationFile == null || logConfigurationFile.isEmpty()) {
            System.setProperty(LOG4J_CONFIGURATION, "log4j.xml");
        }
        _log = LoggerFactory.getLogger(L2LoginServer.class);
    }
}
