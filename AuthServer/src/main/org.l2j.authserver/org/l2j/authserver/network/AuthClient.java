package org.l2j.authserver.network;

import com.l2jbr.commons.Config;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.crypt.AuthCrypt;
import org.l2j.authserver.network.crypt.ScrambledKeyPair;
import org.l2j.authserver.network.packet.auth2client.Init;
import org.l2j.authserver.network.packet.L2LoginServerPacket;
import org.l2j.authserver.network.packet.auth2client.LoginFail;
import org.l2j.authserver.network.packet.auth2client.LoginFail.LoginFailReason;
import org.l2j.authserver.network.packet.auth2client.PlayFail;
import org.l2j.authserver.network.packet.auth2client.PlayFail.PlayFailReason;
import org.l2j.mmocore.Client;
import org.l2j.mmocore.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

/**
 * Represents a client connected into the LoginServer
 */
public final class AuthClient extends Client<Connection<AuthClient>> {

    private static Logger _log = LoggerFactory.getLogger(AuthClient.class);

    private final long _connectionStartTime;

    private AuthCrypt _authCrypt;
    private ScrambledKeyPair _scrambledPair;
    private byte[] _blowfishKey;
    private int _sessionId;
    private boolean _joinedGS;
    private SessionKey _sessionKey;

    private String _account;
    private int _accessLevel;
    private int _lastServer;
    private boolean _usesInternalIP;
    private LoginClientState _state;

    public AuthClient(Connection<AuthClient> con) {
		super(con);
		_state = LoginClientState.CONNECTED;
		String ip = getHostAddress();

		// TODO unhardcode this
		if (ip.startsWith("192.168") || ip.startsWith("10.0") || ip.startsWith("127.0.0.1")) {
			_usesInternalIP = true;
		}

		_connectionStartTime = System.currentTimeMillis();

		AuthController.getInstance().registerClient(this);
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

    public void setCrypter(AuthCrypt crypt) {
        _authCrypt =  crypt;
    }

	public boolean usesInternalIP()
	{
		return _usesInternalIP;
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
	
	public LoginClientState getState()
	{
		return _state;
	}
	
	public void setState(LoginClientState state) {
		_state = state;
	}
	
	public byte[] getBlowfishKey() {
		return _blowfishKey;
	}
	
	public byte[] getScrambledModulus() {
		return _scrambledPair._scrambledModulus;
	}
	
	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair._pair.getPrivate();
	}
	
	public String getAccount()
	{
		return _account;
	}
	
	public void setAccount(String account)
	{
		_account = account;
	}
	
	public void setAccessLevel(int accessLevel) {
		_accessLevel = accessLevel;
	}
	
	public int getAccessLevel()
	{
		return _accessLevel;
	}
	
	public void setLastServer(int lastServer)
	{
		_lastServer = lastServer;
	}
	
	public int getLastServer() {
		return _lastServer;
	}
	
	public int getSessionId() {
		return _sessionId;
	}
	
	public boolean hasJoinedGS()
	{
		return _joinedGS;
	}
	
	public void setJoinedGS(boolean val)
	{
		_joinedGS = val;
	}
	
	public void setSessionKey(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}
	
	public SessionKey getSessionKey() {
		return _sessionKey;
	}
	
	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}
	
	public void sendPacket(L2LoginServerPacket lsp) {
	    writePacket(lsp);
	}


    public void close(LoginFailReason reason) {
        close(new LoginFail(reason));
	}
	
	public void close(PlayFailReason reason) {
		close(new PlayFail(reason));
	}

    @Override
    public void onConnected() {
        sendPacket(new Init());
    }

	@Override
	protected void onDisconnection()
	{
        System.out.println("Disconnected");
		if (Config.DEBUG)
		{
			_log.info("DISCONNECTED: " + toString());
		}
		
		if (getState() != LoginClientState.AUTHED_LOGIN)
		{
			AuthController.getInstance().removeClient(this);
		}
		else if (!hasJoinedGS())
		{
			AuthController.getInstance().removeAuthedClient(getAccount());
		}
	}

    @Override
	public String toString() {
		String address =  getHostAddress();
		if (getState() == LoginClientState.AUTHED_LOGIN) {
			return "[" + getAccount() + " (" + (address.equals("") ? "disconnect" : address) + ")]";
		}
		return "[" + (address.equals("") ? "disconnect" : address) + "]";
	}

    public enum LoginClientState {
        CONNECTED,
        AUTHED_GG,
        AUTHED_LOGIN;
    }
}
