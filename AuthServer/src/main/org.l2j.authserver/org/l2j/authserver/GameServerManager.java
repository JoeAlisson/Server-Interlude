package org.l2j.authserver;

import com.l2jbr.commons.database.GameserverRepository;
import com.l2jbr.commons.database.model.GameServer;
import com.l2jbr.commons.util.Rnd;
import org.l2j.authserver.xml.ServerNameReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jbr.commons.database.DatabaseAccess.getRepository;
import static com.l2jbr.commons.util.Util.hexToString;
import static java.util.Objects.isNull;

/**
 * @author KenM
 */
public class GameServerManager {

    private static final Logger _log = LoggerFactory.getLogger(GameServerManager.class);
    private static final int KEYS_SIZE = 10;

    private static GameServerManager _instance;
    private static Map<Integer, String> _serverNames = new HashMap<>();

    private final Map<Integer, GameServerInfo> gameservers = new ConcurrentHashMap<>();
    private KeyPair[] _keyPairs;

    public static void load() throws GeneralSecurityException {
        if (isNull(_instance)) {
            _instance = new GameServerManager();
        }
    }

    public GameServerManager() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        loadServerNames();
        loadRegisteredGameServers();
        loadRSAKeys();
    }

    private void loadServerNames() {
        try {
            var serverNameReader = new ServerNameReader();
            var f = new File("servername.xml");
            serverNameReader.read(f);
            _serverNames  = serverNameReader.getServerNames();
            _log.info("Loaded {} server names", _serverNames.size());
        } catch (Exception e) {
            _log.warn("servername.xml could not be loaded.");
        }
    }

    private void loadRegisteredGameServers() {
        getRepository(GameserverRepository.class).findAll().forEach(gameServer -> {
            GameServerInfo gsi = new GameServerInfo(gameServer);
            gameservers.put(gameServer.getId(), gsi);
        });
        _log.info("Loaded {} registered Game Servers", gameservers.size());
    }

    private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
        keyGen.initialize(spec);

        _keyPairs = new KeyPair[KEYS_SIZE];
        for (int i = 0; i < KEYS_SIZE; i++) {
            _keyPairs[i] = keyGen.genKeyPair();
        }
        _log.info("Cached {} RSA keys for Game Server communication.", _keyPairs.length);
    }

    public Map<Integer, GameServerInfo> getRegisteredGameServers() {
        return gameservers;
    }

    public GameServerInfo getRegisteredGameServerById(int id) {
        return gameservers.get(id);
    }

    public boolean hasRegisteredGameServerOnId(int id) {
        return gameservers.containsKey(id);
    }

    public boolean registerWithFirstAvaliableId(GameServerInfo gsi) {
        // avoid two servers registering with the same "free" id
        synchronized (gameservers) {
            for (Entry<Integer, String> entry : _serverNames.entrySet()) {
                if (!gameservers.containsKey(entry.getKey())) {
                    gameservers.put(entry.getKey(), gsi);
                    gsi.setId(entry.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean register(int id, GameServerInfo gsi) {
        if(isNull(gameservers.putIfAbsent(id, gsi))) {
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
        return _serverNames.get(id);
    }

    public Map<Integer, String> getServerNames() {
        return _serverNames;
    }

    public static GameServerManager getInstance() {
        return _instance;
    }

    public KeyPair getKeyPair() {
        return _keyPairs[Rnd.nextInt(10)];
    }
}