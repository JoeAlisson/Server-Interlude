package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExShowFortressInfo;

public class RequestAllFortressInfo extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        // trigger
    }

    @Override
    protected void runImpl() {
        sendPacket(new ExShowFortressInfo());
    }
}
