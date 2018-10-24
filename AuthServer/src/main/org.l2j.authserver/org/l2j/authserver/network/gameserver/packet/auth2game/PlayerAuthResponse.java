package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends GameServerWritablePacket {

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