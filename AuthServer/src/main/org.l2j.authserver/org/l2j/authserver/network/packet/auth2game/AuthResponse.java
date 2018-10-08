package org.l2j.authserver.network.packet.auth2game;

import org.l2j.authserver.GameServerManager;
import org.l2j.authserver.network.packet.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class AuthResponse extends ServerBasePacket {

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
