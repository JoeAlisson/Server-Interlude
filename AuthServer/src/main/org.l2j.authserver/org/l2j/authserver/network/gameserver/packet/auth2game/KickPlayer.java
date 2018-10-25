package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

public class KickPlayer extends GameServerWritablePacket {

    private final String account;

    public KickPlayer(String account) {
        this.account = account;
	}

	@Override
	protected void writeImpl() {
		writeByte(0x04);
		writeString(account);
	}
}