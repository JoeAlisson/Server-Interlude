package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.client.packet.ClientBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthRequest extends ClientBasePacket {
	
	private final String _account;
	private final SessionKey _sessionKey;

	public PlayerAuthRequest(byte[] data) {
		super(data);
		_account = readString();
		int sessionId = readInt();
		int serverAccountId = readInt();
		int authAccountId = readInt();
		int authKey = readInt();
		_sessionKey = new SessionKey(authAccountId, authKey, sessionId, serverAccountId);
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