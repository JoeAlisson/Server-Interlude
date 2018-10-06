package com.l2jbr.loginserver.network.gameserverpackets;

import com.l2jbr.loginserver.network.clientpackets.ClientBasePacket;

/**
 * @author -Wooden-
 */
public class ChangeAccessLevel extends ClientBasePacket {
	
	private final int _level;
	private final String _account;

	public ChangeAccessLevel(byte[] data) {
		super(data);
		_level = readInt();
		_account = readString();
	}

	public String getAccount()
	{
		return _account;
	}

	public int getLevel()
	{
		return _level;
	}
}