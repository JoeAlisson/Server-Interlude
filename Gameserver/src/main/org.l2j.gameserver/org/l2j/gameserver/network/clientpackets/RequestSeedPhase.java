package org.l2j.gameserver.network.clientpackets;


import org.l2j.gameserver.network.serverpackets.ExShowSeedMapInfo;

public class RequestSeedPhase extends L2GameClientPacket {

    @Override
    protected void readImpl() { }

    @Override
    protected void runImpl() {
        sendPacket(new ExShowSeedMapInfo());
    }
}
