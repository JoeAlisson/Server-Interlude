package org.l2j.authserver.network.loginserverpackets;

import org.l2j.authserver.GameServerManager;
import org.l2j.authserver.network.serverpackets.ServerBasePacket;

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
