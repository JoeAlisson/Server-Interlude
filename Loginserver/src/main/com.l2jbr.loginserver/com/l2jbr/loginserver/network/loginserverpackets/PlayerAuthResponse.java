package com.l2jbr.loginserver.network.loginserverpackets;

import com.l2jbr.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends ServerBasePacket {

	public PlayerAuthResponse(String account, boolean response) {
		writeByte(0x03);
		writeString(account);
		writeByte(response ? 1 : 0);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}