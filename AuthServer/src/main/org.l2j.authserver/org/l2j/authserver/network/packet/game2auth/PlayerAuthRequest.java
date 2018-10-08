package org.l2j.authserver.network.packet.game2auth;

import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.packet.ClientBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthRequest extends ClientBasePacket {
	
	private final String _account;
	private final SessionKey _sessionKey;

	public PlayerAuthRequest(byte[] data) {
		super(data);
		_account = readString();
		int playKey1 = readInt();
		int playKey2 = readInt();
		int loginKey1 = readInt();
		int loginKey2 = readInt();
		_sessionKey = new SessionKey(loginKey1, loginKey2, playKey1, playKey2);
	}

	public String getAccount()
	{
		return _account;
	}

	public SessionKey getKey()
	{
		return _sessionKey;
	}
	
}