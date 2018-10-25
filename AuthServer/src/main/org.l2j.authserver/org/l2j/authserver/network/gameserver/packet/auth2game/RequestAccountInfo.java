package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

public class RequestAccountInfo extends GameServerWritablePacket {

    private final String account;

    public RequestAccountInfo(String account) {
        this.account = account;
    }

    @Override
    protected void writeImpl() {
        writeByte(0x05);
        writeString(account);
    }
}
