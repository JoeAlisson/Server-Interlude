package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

public class PlayerAuthResponse extends GameServerWritablePacket {

	private final String account;
	private final int response;

	public PlayerAuthResponse(String account, int response) {
		this.account = account;
		this.response = response;
	}


	@Override
	protected void writeImpl()  {
		writeByte(0x03);
		writeString(account);
		writeByte(response);
	}
}