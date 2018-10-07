package com.l2jbr.loginserver;

import com.l2jbr.commons.Config;
import com.l2jbr.commons.Server;
import com.l2jbr.commons.status.Status;
import com.l2jbr.loginserver.network.*;
import org.l2j.mmocore.ConnectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import static com.l2jbr.loginserver.settings.LoginServerSettings.*;
import static java.util.Objects.nonNull;

/**
 * @author KenM
 */
public class AuthServer {

    public static final int PROTOCOL_REV = 0x0102;
    private static final String LOG4J_CONFIGURATION = "log4j.configurationFile";

    private static AuthServer _instance;
    private static Logger _log;
    private GameServerListener _gameServerListener;

    public static void main(String[] args) {
        configureLogger();
        Server.serverMode = Server.MODE_LOGINSERVER;
        Config.load();
        _instance = new AuthServer();
    }

    public static AuthServer getInstance() {
        return _instance;
    }

    public AuthServer() {
        try {
            LoginController.load();
        } catch (GeneralSecurityException e) {
            _log.error("FATAL: Failed initializing LoginController. Reason: {}", e.getLocalizedMessage());
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
            _log.info("Listening for GameServers on {} : {}", gameServerListenHost(), gameServerListenPort());
        } catch (IOException e) {
            _log.error("FATAL: Failed to start the Game Server Listener. Reason: " + e.getMessage());
            if (Config.DEVELOPER) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        InetSocketAddress bindAddress;
        if (!loginListenHost().equals("*")) {
            bindAddress = new InetSocketAddress(loginListenHost(), loginListenPort());
        } else {
            bindAddress = new InetSocketAddress(loginListenPort());
        }

        try {
            final L2LoginPacketHandler lph = new L2LoginPacketHandler();
            final SelectorHelper sh = new SelectorHelper();

            var connectionHandler = ConnectionBuilder.create(bindAddress, sh, lph, sh).threadPoolSize(2).build();
            connectionHandler.start();
        } catch (IOException e) {
            _log.error("FATAL: Failed to open ConnectionHandler. Reason: " + e.getMessage(), e);
            System.exit(1);
        }
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
        _log = LoggerFactory.getLogger(AuthServer.class);
    }
}
