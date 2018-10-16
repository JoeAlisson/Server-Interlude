package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.serverpackets.ExUISettingPacket;

import static java.util.Objects.isNull;

public class RequestKeyMapping extends L2GameClientPacket {

	@Override
	protected void readImpl() {}

	@Override
	protected void runImpl() {
		var activeChar = client.getActiveChar();
		if(isNull(activeChar))
			return;

		activeChar.sendPacket(new ExUISettingPacket(activeChar));
	}

	@Override
	public String getType() {
		return "[C] 0xD0 0x21 Request Key Mapping";
	}
}