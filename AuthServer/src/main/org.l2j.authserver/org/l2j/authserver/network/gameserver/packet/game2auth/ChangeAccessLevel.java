package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.network.client.packet.ClientBasePacket;

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