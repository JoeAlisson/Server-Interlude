package com.l2jbr.loginserver.network.loginserverpackets;

import com.l2jbr.loginserver.GameServerTable;
import com.l2jbr.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class AuthResponse extends ServerBasePacket {

	public AuthResponse(int serverId) {
		writeByte(0x02);
		writeByte(serverId);
		writeString(GameServerTable.getInstance().getServerNameById(serverId));
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
