package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.serverpackets.CharTemplates;

public final class RequestNewCharacter extends L2GameClientPacket {

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        sendPacket(new CharTemplates());
    }
}
