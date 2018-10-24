package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

/**
 * @author -Wooden-
 */
public class AuthResponse extends GameServerWritablePacket {

	public AuthResponse(int serverId) {
		writeByte(0x02);
		writeByte(serverId);
		writeString(GameServerManager.getInstance().getServerNameById(serverId));
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
