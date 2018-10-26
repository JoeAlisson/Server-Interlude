package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.controller.GameServerManager;

public class AuthResponse extends GameServerWritablePacket {

	private final int serverId;

	public AuthResponse(int serverId) {
		this.serverId = serverId;
	}

	@Override
	protected void writeImpl() {
		writeByte(0x02);
		writeByte(serverId);
		writeString(GameServerManager.getInstance().getServerNameById(serverId));
	}
}
