package org.l2j.gameserver.network.loginserverpackets;

public class RequestAccountInfo extends LoginServerBasePacket {

    private final String account;

    public RequestAccountInfo(byte[] decrypt) {
        super(decrypt);
        account = readS();
    }

    public String getAccount() {
        return account;
    }
}
