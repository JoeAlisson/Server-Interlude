package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.serverpackets.Ex2ndPasswordCheckPacket;

import static com.l2jbr.gameserver.serverpackets.Ex2ndPasswordCheckPacket.PASSWORD_OK;

public class RequestEx2ndPasswordCheck extends L2GameClientPacket
{
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        // TODO Implements to factory Pwd
        sendPacket(new Ex2ndPasswordCheckPacket(PASSWORD_OK));
    }

    @Override
    public String getType() {
        return "[C] Request 2nd Password Check";
    }
}