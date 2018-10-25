package org.l2j.authserver.network.gameserver;

import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.client.packet.GameServerWritablePacket;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.authserver.network.gameserver.packet.auth2game.InitLS;
import org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail;
import org.l2j.commons.crypt.NewCrypt;
import org.l2j.commons.database.model.Account;
import org.l2j.mmocore.Client;
import org.l2j.mmocore.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.authserver.network.gameserver.ServerClientState.AUTHED;
import static org.l2j.authserver.network.gameserver.ServerClientState.CONNECTED;

public final class ServerClient extends Client<Connection<ServerClient>> {

    private static Logger _log = LoggerFactory.getLogger(ServerClient.class);
    private final Map<Integer,Integer> charactersOnServer = new HashMap<>();
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private NewCrypt blowfish;
    private ServerClientState state;

    private AuthCrypt _authCrypt;
    private ScrambledKeyPair _scrambledPair;
    private byte[] _blowfishKey;
    private int _sessionId;
    private SessionKey _sessionKey;

    private Account account;
    private boolean _usesInternalIP;

    private boolean isJoinedGameSever;
    private GameServerInfo gameServerInfo;


    public ServerClient(Connection<ServerClient> con) {
		super(con);
        KeyPair pair = GameServerManager.getInstance().getKeyPair();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
	}

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getRSAPrivateKey() {
        return privateKey;
    }

    public void setBlowfish(NewCrypt newCrypt) {
        this.blowfish = newCrypt;

    }

    public void setState(ServerClientState state) {
        this.state = state;
    }

    public ServerClientState getState()
    {
        return state;
    }

    public void close(int reason) {
        close(new LoginGameServerFail(reason));
    }

    public void setGameServerInfo(GameServerInfo gsi) {
        this.gameServerInfo = gsi;
    }

    public GameServerInfo getGameServerInfo() {
        return gameServerInfo;
    }


    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        boolean decrypted;
        try  {
            decrypted = _authCrypt.decrypt(data, offset, size);
        }
        catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
            disconnect();
            return false;
        }

        if (!decrypted) {
            _log.warn("Wrong checksum from client: {}", toString());
            disconnect();
        }
        return decrypted;
    }

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        int encryptedSize = -1;
	    try {
	       encryptedSize = _authCrypt.encrypt(data, offset, size);
        } catch (IOException e) {
	        _log.error(e.getLocalizedMessage(), e);
	        return encryptedSize;
        }
        return encryptedSize;
    }

	public void sendPacket(GameServerWritablePacket lsp) {
	    writePacket(lsp);
	}

    @Override
    public void onConnected() {
        setState(CONNECTED);
        sendPacket(new InitLS());
    }

	@Override
	protected void onDisconnection() {

        String serverName = getHostAddress();
        var serverId = gameServerInfo.getId();

        if(serverId != -1) {
            serverName = String.format("[%d] %s", serverId, GameServerManager.getInstance().getServerNameById(serverId));
        }
        _log.info("GameServer {}: Connection Lost", serverName);

        if (AUTHED == state) {
            gameServerInfo.setDown();
            _log.info("Server [{}] {} is now set as disconnect", serverId, GameServerManager.getInstance().getServerNameById(serverId));
        }
	}


}
