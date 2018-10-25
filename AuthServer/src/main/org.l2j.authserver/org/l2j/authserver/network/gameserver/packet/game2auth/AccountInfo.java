package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.gameserver.packet.GameserverReadablePacket;

public class AccountInfo extends GameserverReadablePacket {

    private  int players;
    private  String account;

    @Override
    protected void readImpl()   {
        account = readString();
        players = readByte();
    }

    @Override
    protected void runImpl()  {
        AuthController.getInstance().addAccountCharactersInfo(client.getGameServerInfo().getId(), account, players);
    }
}
