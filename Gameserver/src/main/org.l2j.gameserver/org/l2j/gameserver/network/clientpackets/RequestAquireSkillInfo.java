/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.datatables.SkillSpellbookTable;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.datatables.SkillTreeTable;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.actor.instance.L2FolkInstance;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.ClanSkillInfo;
import org.l2j.gameserver.model.entity.database.SkillInfo;
import org.l2j.gameserver.network.serverpackets.AquireSkillInfoPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * This class ...
 * @version $Revision: 1.5.2.1.2.5 $ $Date: 2005/04/06 16:13:48 $
 */
public class RequestAquireSkillInfo extends L2GameClientPacket
{
	private static final String _C__6B_REQUESTAQUIRESKILLINFO = "[C] 6B RequestAquireSkillInfo";
	private static Logger _log = LoggerFactory.getLogger(RequestAquireSkillInfo.class.getName());
	
	private int _id;
	private int _level;
	private int _skillType;
	
	@Override
	protected void readImpl()
	{
		_id = readInt();
		_level = readInt();
		_skillType = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2FolkInstance trainer = activeChar.getLastFolkNPC();
		
		if (((trainer == null) || !activeChar.isInsideRadius(trainer, L2NpcInstance.INTERACTION_DISTANCE, false, false)) && !activeChar.isGM())
		{
			return;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(_id, _level);
		
		boolean canteach = false;
		
		if (skill == null)
		{
			if (Config.DEBUG)
			{
				_log.warn("skill id " + _id + " level " + _level + " is undefined. aquireSkillInfo failed.");
			}
			return;
		}
		
		if (_skillType == 0)
		{
			if (!trainer.canTeach(activeChar.getSkillLearningClassId()))
			{
				return; // cheater
			}
			
			List<SkillInfo> skills = SkillTreeTable.getInstance().getAvailableSkills(activeChar, PlayerTemplateTable.getInstance().getClassTemplate(activeChar.getSkillLearningClassId().getId()));
			
			for (SkillInfo s : skills)
			{
				if ((s.getId() == _id) && (s.getLevel() == _level))
				{
					canteach = true;
					break;
				}
			}
			
			if (!canteach)
			{
				return; // cheater
			}
			
			int requiredSp = SkillTreeTable.getInstance().getSkillCost(activeChar, skill);
			AquireSkillInfoPacket asi = new AquireSkillInfoPacket(skill.getId(), skill.getLevel(), requiredSp, 0);
			
			if (Config.SP_BOOK_NEEDED)
			{
				int spbId = SkillSpellbookTable.getInstance().getBookForSkill(skill);
				
				if ((skill.getLevel() == 1) && (spbId > -1))
				{
					asi.addRequirement(99, spbId, 1, 50);
				}
			}
			
			sendPacket(asi);
		}
		else if (_skillType == 2)
		{
			int requiredRep = 0;
			int itemId = 0;
			List<ClanSkillInfo> skills = SkillTreeTable.getInstance().getAvailablePledgeSkills(activeChar);
			
			for (ClanSkillInfo s : skills)
			{
				if ((s.getId() == _id) && (s.getLevel() == _level))
				{
					canteach = true;
					requiredRep = s.getRepCost();
					itemId = s.getItemId();
					break;
				}
			}
			
			if (!canteach)
			{
				return; // cheater
			}
			
			AquireSkillInfoPacket asi = new AquireSkillInfoPacket(skill.getId(), skill.getLevel(), requiredRep, 2);
			
			if (Config.LIFE_CRYSTAL_NEEDED)
			{
				asi.addRequirement(1, itemId, 1, 0);
			}
			
			sendPacket(asi);
		}
		else
		// Common Skills
		{
			int costid = 0;
			int costcount = 0;
			int spcost = 0;
			
			List<SkillInfo> skillsc = SkillTreeTable.getInstance().getAvailableSkills(activeChar);
			
			for (SkillInfo s : skillsc)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				
				if ((sk == null) || (sk != skill))
				{
					continue;
				}
				
				canteach = true;
				costid = s.getCostId();
				costcount = s.getCostCount();
				spcost = s.getSpCost();
			}
			
			AquireSkillInfoPacket asi = new AquireSkillInfoPacket(skill.getId(), skill.getLevel(), spcost, 1);
			asi.addRequirement(4, costid, costcount, 0);
			sendPacket(asi);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__6B_REQUESTAQUIRESKILLINFO;
	}
}