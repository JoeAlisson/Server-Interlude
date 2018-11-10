package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.CharTemplates;

public final class RequestNewCharacter extends L2GameClientPacket {

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        sendPacket(new CharTemplates());
    }
}
