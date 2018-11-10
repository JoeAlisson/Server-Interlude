package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.CharNameTable;
import org.l2j.gameserver.network.serverpackets.ExIsCharNameCreatable;

import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.network.serverpackets.ExIsCharNameCreatable.*;

public class RequestCharacterNameCreatable extends L2GameClientPacket {

    private String _charname;

    @Override
    protected void readImpl() {
        _charname = readString();
    }

    @Override
    protected void runImpl() {
        if (isNullOrEmpty(_charname)) {
            sendPacket(new ExIsCharNameCreatable(ENTER_CHAR_NAME__MAX_16_CHARS));
            return;
        }

        if (!_charname.matches(Config.CNAME_TEMPLATE)) {
            sendPacket(new ExIsCharNameCreatable(WRONG_NAME));
            return;
        }

        if (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT > 0 && CharNameTable.accountCharNumber(_charname) > Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) {
            sendPacket(new ExIsCharNameCreatable(TOO_MANY_CHARACTERS));
            return;
        }

        if (CharNameTable.doesCharNameExist(_charname)) {
            sendPacket(new ExIsCharNameCreatable(NAME_ALREADY_EXISTS));
            return;
        }
        sendPacket(new ExIsCharNameCreatable(SUCCESS));
    }
}
