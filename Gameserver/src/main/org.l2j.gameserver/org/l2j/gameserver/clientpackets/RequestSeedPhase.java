package org.l2j.gameserver.clientpackets;


import org.l2j.gameserver.serverpackets.ExShowSeedMapInfo;

public class RequestSeedPhase extends L2GameClientPacket {

    @Override
    protected void readImpl() { }

    @Override
    protected void runImpl() {
        sendPacket(new ExShowSeedMapInfo());
    }
}
