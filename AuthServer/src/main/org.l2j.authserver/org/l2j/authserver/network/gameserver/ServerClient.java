package org.l2j.authserver.network.gameserver;

import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.gameserver.packet.auth2game.GameServerWritablePacket;
import org.l2j.authserver.network.crypt.AuthServerCrypt;
import org.l2j.authserver.network.gameserver.packet.auth2game.InitLS;
import org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail;
import org.l2j.mmocore.Client;
import org.l2j.mmocore.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.l2j.authserver.network.gameserver.ServerClientState.AUTHED;
import static org.l2j.authserver.network.gameserver.ServerClientState.CONNECTED;

public final class ServerClient extends Client<Connection<ServerClient>> {

    private static Logger _log = LoggerFactory.getLogger(ServerClient.class);
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private AuthServerCrypt crypt;
    private ServerClientState state;
    private GameServerInfo gameServerInfo;

    public ServerClient(Connection<ServerClient> con) {
		super(con);
	}

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getRSAPrivateKey() {
        return privateKey;
    }

    public void setCryptKey(byte[] key) {
        crypt.setKey(key);
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
        try  {
             return crypt.decrypt(data, offset, size);
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
            disconnect();
            return false;
        }
    }

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        try {
            return  crypt.encrypt(data, offset, size);
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
            return -1;
        }
    }

	public void sendPacket(GameServerWritablePacket lsp) {
	    writePacket(lsp);
	}

    @Override
    public void onConnected() {
        setState(CONNECTED);
        var pair = GameServerManager.getInstance().getKeyPair();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
        crypt =  new AuthServerCrypt();
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