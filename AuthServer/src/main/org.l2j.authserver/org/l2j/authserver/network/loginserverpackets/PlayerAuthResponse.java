package org.l2j.authserver.network.loginserverpackets;

import org.l2j.authserver.network.serverpackets.ServerBasePacket;

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