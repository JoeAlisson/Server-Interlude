package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.network.client.packet.ClientBasePacket;

public class AccountInfo extends ClientBasePacket {

    private final int players;
    private final String account;

    public AccountInfo(byte[] data) {
        super(data);
        account = readString();
        players = readByte();
    }

    public String getAccount() {
        return account;
    }

    public int getPlayers() {
        return players;
    }
}
