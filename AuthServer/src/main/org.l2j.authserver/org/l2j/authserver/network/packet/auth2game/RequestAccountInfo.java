package org.l2j.authserver.network.packet.auth2game;

import org.l2j.authserver.network.packet.ServerBasePacket;

import java.io.IOException;

public class RequestAccountInfo extends ServerBasePacket {

    public RequestAccountInfo(String account) {
        writeByte(0x05);
        writeString(account);
    }


    @Override
    public byte[] getContent() throws IOException {
        return getBytes();
    }
}
