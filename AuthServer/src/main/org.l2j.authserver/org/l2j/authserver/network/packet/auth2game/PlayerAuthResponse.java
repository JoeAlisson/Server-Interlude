package org.l2j.authserver.network.packet.auth2game;

import org.l2j.authserver.network.packet.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends ServerBasePacket {

	public PlayerAuthResponse(String account, int response) {
		writeByte(0x03);
		writeString(account);
		writeByte(response);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}