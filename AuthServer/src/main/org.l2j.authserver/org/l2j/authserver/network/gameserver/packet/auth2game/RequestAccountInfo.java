package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

import java.io.IOException;

public class RequestAccountInfo extends GameServerWritablePacket {

    public RequestAccountInfo(String account) {
        writeByte(0x05);
        writeString(account);
    }


    @Override
    public byte[] getContent() throws IOException {
        return getBytes();
    }
}
