package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.serverpackets.ExUserBanInfo;

public class RequestUserBanInfo extends L2GameClientPacket {

    @Override
    protected void readImpl()
    {
        var data = readInt();
    }

    @Override
    protected void runImpl()
    {
        sendPacket(new ExUserBanInfo(5));
    }
}