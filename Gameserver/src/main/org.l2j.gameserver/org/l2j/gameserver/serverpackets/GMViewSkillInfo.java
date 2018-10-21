package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class GMViewSkillInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private L2Skill[] _skills;
	
	public GMViewSkillInfo(L2PcInstance cha)
	{
		_activeChar = cha;
		_skills = _activeChar.getAllSkills();
		if (_skills.length == 0)
		{
			_skills = new L2Skill[0];
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x91);
		writeString(_activeChar.getName());
		writeInt(_skills.length);
		
		for (L2Skill skill : _skills)
		{
			writeInt(skill.isPassive() ? 1 : 0);
			writeInt(skill.getLevel());
			writeInt(skill.getId());
			writeByte(0x00); // c5
		}
	}
}