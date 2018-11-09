package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.serverpackets.Ex2ndPasswordCheckPacket;

import static org.l2j.gameserver.serverpackets.Ex2ndPasswordCheckPacket.PASSWORD_NEW;
import static org.l2j.gameserver.serverpackets.Ex2ndPasswordCheckPacket.PASSWORD_OK;
import static org.l2j.gameserver.serverpackets.Ex2ndPasswordCheckPacket.PASSWORD_PROMPT;

public class RequestEx2ndPasswordCheck extends L2GameClientPacket
{
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        if(client.isSecondFactorAuthed()) {
            sendPacket(new Ex2ndPasswordCheckPacket(PASSWORD_OK));
        } else if(client.hasSecondPassword()) {
            sendPacket(new Ex2ndPasswordCheckPacket(PASSWORD_PROMPT));
        } else {
            sendPacket(new Ex2ndPasswordCheckPacket(PASSWORD_NEW));
        }
    }
}