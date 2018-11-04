package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.ClanTable;
import org.l2j.gameserver.factory.PlayerFactory;
import org.l2j.gameserver.model.entity.database.Character;
import org.l2j.gameserver.serverpackets.CharDeleteFail;
import org.l2j.gameserver.serverpackets.CharDeleteOk;
import org.l2j.gameserver.serverpackets.CharSelectInfo;

import static java.util.Objects.nonNull;

public final class CharacterDelete extends L2GameClientPacket  {

	private int charSlot;

	@Override
	protected void readImpl()
	{
		charSlot = readInt();
	}
	
	@Override
	protected void runImpl() {
		Character character = client.getCharacterForSlot(charSlot);
		if(nonNull(character)) {
            if (character.getClanId() > 0) {
                var clan = ClanTable.getInstance().getClan(character.getClanId());
                if (nonNull(clan) && clan.getLeaderId() == character.getObjectId()) {
                    sendPacket(new CharDeleteFail(CharDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
                } else {
                    sendPacket(new CharDeleteFail(CharDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
                }
                return;
            }

            if (Config.DELETE_DAYS == 0) {
                PlayerFactory.delete(character.getObjectId());
                client.removeCharacter(character);
            } else {
                PlayerFactory.markToDelete(character);
            }

            sendPacket(new CharDeleteOk());
        }
		int nextSlot = charSlot > 0 ? charSlot -1 : client.getCharacters().size() -1;
		CharSelectInfo cl = new CharSelectInfo(client.getCharacters(), nextSlot);
		sendPacket(cl);
	}
}
