
package org.l2j.authserver.network.packet.game2auth;

import org.l2j.authserver.network.packet.ClientBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerLogout extends ClientBasePacket {
	
	private final String _account;

	public PlayerLogout(byte[] data) {
		super(data);
		_account = readString();
	}

	public String getAccount()
	{
		return _account;
	}
	
}