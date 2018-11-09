package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.CharNameTable;
import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.factory.PlayerFactory;
import org.l2j.gameserver.serverpackets.CharCreateFail;
import org.l2j.gameserver.serverpackets.CharCreateOk;
import org.l2j.gameserver.templates.ClassTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.Util.isValidPlayerName;

public final class CharacterCreate extends L2GameClientPacket {

    private static Logger logger = LoggerFactory.getLogger(CharacterCreate.class);

    private String _name;
    private byte _sex;
    private int _classId;
    private byte _hairStyle;
    private byte _hairColor;
    private byte _face;

    @Override
    protected void readImpl() {
        _name = readString();
        readInt(); // _race
        _sex = (byte) readInt();
        _classId = readInt();
        readInt(); // int
        readInt(); // str
        readInt(); // _con
        readInt(); // _men
        readInt(); // _dex
        readInt(); // _wit
        _hairStyle = (byte) readInt();
        _hairColor = (byte) readInt();
        _face = (byte) readInt();
    }

    @Override
    protected void runImpl() {
        if (!isValidPlayerName(_name)) {
            logger.debug("reader name {} is invalid. creation failed.", _name);
            sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
            return;
        }

        if ((CharNameTable.accountCharNumber(client.getAccount()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0)) {
            logger.debug("Max number of characters ({}) reached. Creation failed.", Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
            sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
            return;
        }

        if (CharNameTable.doesCharNameExist(_name)) {
            logger.debug("reader name  {} already exists. creation failed.", _name);
            sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
            return;
        }

        logger.debug("reader name: {}  classId: {}", _name, _classId);

        ClassTemplate template = PlayerTemplateTable.getInstance().getClassTemplate(_classId);
        if ((isNull(template))) {
            sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
            return;
        }

        var character = PlayerFactory.create(template, getClient().getAccount(), _name, _hairStyle, _hairColor, _face, _sex);
        if (isNull(character)) {
            sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
            return;
        }
        client.addCharacter(character);
        sendPacket(new CharCreateOk());
    }
}
