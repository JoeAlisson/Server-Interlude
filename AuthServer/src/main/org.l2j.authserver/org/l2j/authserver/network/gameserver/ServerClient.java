package org.l2j.authserver.network.gameserver;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.client.AuthClientState;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
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

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.client.AuthClientState.AUTHED_LOGIN;

public final class ServerClient extends Client<Connection<ServerClient>> {

    private static Logger _log = LoggerFactory.getLogger(ServerClient.class);
    private final Map<Integer,Integer> charactersOnServer = new HashMap<>();
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private NewCrypt blowfish;

    private AuthCrypt _authCrypt;
    private ScrambledKeyPair _scrambledPair;
    private byte[] _blowfishKey;
    private int _sessionId;
    private SessionKey _sessionKey;

    private Account account;
    private boolean _usesInternalIP;
    private AuthClientState _state;
    private boolean isJoinedGameSever;


    public ServerClient(Connection<ServerClient> con) {
		super(con);
        KeyPair pair = GameServerManager.getInstance().getKeyPair();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
	}

    public PrivateKey getRSAPrivateKey() {
        return privateKey;
    }

    public void setBlowfish(NewCrypt newCrypt) {
        this.blowfish = newCrypt;

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

/*	public void sendPacket(L2LoginServerPacket lsp) {
	    writePacket(lsp);
	}

    public void close(LoginFailReason reason) {
        close(new LoginFail(reason));
	}

	public void close(PlayFailReason reason) {
		close(new PlayFail(reason));
	}

	public void close(AccountKicked.AccountKickedReason reason) {
        close(new AccountKicked(reason));
	}*/

    @Override
    public void onConnected() {
        /*sendPacket(new Init());*/
    }

	@Override
	protected void onDisconnection() {
        _log.info("DISCONNECTED: {}", toString());




	}

    public void addCharactersOnServer(int serverId, int players) {
        charactersOnServer.put(serverId, players);
    }

    public int getPlayersOnServer(int serverId) {
        return charactersOnServer.getOrDefault(serverId, 0);
    }

    public void joinGameserver() {
        isJoinedGameSever = true;
    }

    AuthClientState getState()
    {
        return _state;
    }

    public void setState(AuthClientState state) {
        _state = state;
    }

    public byte[] getBlowfishKey() {
        return _blowfishKey;
    }

    public byte[] getScrambledModulus() {
        return _scrambledPair.getScrambledModulus();
    }



    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getAccessLevel() {
        return nonNull(account) ? account.getAccessLevel() : -1;
    }

    public int getLastServer() {
        return nonNull(account) ? account.getLastServer(): -1;
    }

    public int getSessionId() {
        return _sessionId;
    }

    public void setSessionKey(SessionKey sessionKey)
    {
        _sessionKey = sessionKey;
    }

    public SessionKey getSessionKey() {
        return _sessionKey;
    }

    public void setKeyPar(ScrambledKeyPair keyPair) {
        _scrambledPair = keyPair;
    }

    public void setBlowfishKey(byte[] blowfishKey) {
        _blowfishKey = blowfishKey;
    }

    public void setSessionId(int sessionId) {
        _sessionId = sessionId;
    }

    public void setCrypt(AuthCrypt crypt) {
        _authCrypt =  crypt;
    }

    public boolean usesInternalIP()
    {
        return _usesInternalIP;
    }

    @Override
    public String toString() {
        String address =  getHostAddress();
        if (getState() == AUTHED_LOGIN) {
            return "[" + getAccount().getId() + " (" + (address.equals("") ? "disconnect" : address) + ")]";
        }
        return "[" + (address.equals("") ? "disconnect" : address) + "]";
    }

    public void close(int reason) {
        close(new LoginGameServerFail(reason));
    }
}
