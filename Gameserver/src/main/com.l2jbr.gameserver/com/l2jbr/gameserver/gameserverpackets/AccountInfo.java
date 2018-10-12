package com.l2jbr.gameserver.gameserverpackets;

public class AccountInfo extends GameServerBasePacket {

    public AccountInfo(String account, int playersInAccount) {
        writeC(0x07);
        writeS(account);
        writeC(playersInAccount);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}