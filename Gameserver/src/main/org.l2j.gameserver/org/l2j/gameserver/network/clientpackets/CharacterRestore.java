package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.factory.PlayerFactory;
import org.l2j.gameserver.model.entity.database.Character;
import org.l2j.gameserver.network.serverpackets.CharSelectInfo;

public final class CharacterRestore extends L2GameClientPacket {
	private int charSlot;
	
	@Override
	protected void readImpl()
	{
		charSlot = readInt();
	}
	
	@Override
	protected void runImpl() {
        Character character = client.getCharacterForSlot(charSlot);
        PlayerFactory.removeMarkDelete(character);
		sendPacket(new CharSelectInfo(charSlot));
	}
}
