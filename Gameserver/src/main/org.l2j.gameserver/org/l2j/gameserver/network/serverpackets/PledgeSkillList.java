package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Skill;

/**
 * Format: (ch) d [dd]
 * @author -Wooden-
 */
public class PledgeSkillList extends L2GameServerPacket
{
	private final L2Clan _clan;
	
	public PledgeSkillList(L2Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	protected void writeImpl()
	{
		L2Skill[] skills = _clan.getAllSkills();
		
		writeByte(0xfe);
		writeShort(0x39);
		writeInt(skills.length);
		for (L2Skill sk : skills)
		{
			writeInt(sk.getId());
			writeInt(sk.getLevel());
		}
	}
}