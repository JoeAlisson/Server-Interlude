package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.model.entity.database.repository.CharacterRepository;
import com.l2jbr.gameserver.serverpackets.ExIsCharNameCreatable;

import static com.l2jbr.commons.database.DatabaseAccess.getRepository;
import static com.l2jbr.commons.util.Util.isNullOrEmpty;
import static com.l2jbr.gameserver.serverpackets.ExIsCharNameCreatable.*;

public class RequestCharacterNameCreatable extends L2GameClientPacket {

    private String _charname;

	@Override
	protected void readImpl()
	{
		_charname = readString();
	}

	@Override
	protected void runImpl() {
	    var repository = getRepository(CharacterRepository.class);
		if(repository.countByAccount(client.getAccountName()) > Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) {
		    sendPacket(new ExIsCharNameCreatable(TOO_MANY_CHARACTERS));
		    return;
        }

		if(isNullOrEmpty(_charname)) {
			sendPacket(new ExIsCharNameCreatable(ENTER_CHAR_NAME__MAX_16_CHARS));
			return;
		}

		if(!_charname.matches(Config.CNAME_TEMPLATE)) {
			sendPacket(new ExIsCharNameCreatable(WRONG_NAME));
			return;
		}

		if(repository.existsByName(_charname)) {
			sendPacket(new ExIsCharNameCreatable(NAME_ALREADY_EXISTS));
			return;
		}
		sendPacket(new ExIsCharNameCreatable(SUCCESS));
	}

	@Override
	public String getType() {
		return "[C] CHARACTER_NAME_CREATABLE";
	}
}