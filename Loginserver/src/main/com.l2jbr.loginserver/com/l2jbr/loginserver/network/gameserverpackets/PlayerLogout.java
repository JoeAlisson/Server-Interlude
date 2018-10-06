
package com.l2jbr.loginserver.network.gameserverpackets;

import com.l2jbr.loginserver.network.clientpackets.ClientBasePacket;

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