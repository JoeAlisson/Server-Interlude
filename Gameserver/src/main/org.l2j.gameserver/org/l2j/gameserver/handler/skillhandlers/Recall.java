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
package org.l2j.gameserver.handler.skillhandlers;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.MapRegionTable;
import org.l2j.gameserver.handler.ISkillHandler;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.L2Skill.SkillType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.TvTEvent;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.SystemMessage;


public class Recall implements ISkillHandler
{
	// private static Logger logger = LoggerFactory.getLogger(Recall.class.getName());
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.RECALL
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar instanceof L2PcInstance)
		{
			// Thanks nbd
			if (!TvTEvent.onEscapeUse(((L2PcInstance) activeChar).getName()))
			{
				((L2PcInstance) activeChar).sendPacket(new ActionFailed());
				return;
			}
			
			if (((L2PcInstance) activeChar).isInOlympiadMode())
			{
				((L2PcInstance) activeChar).sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
				return;
			}
		}
		
		try
		{
			for (int index = 0; index < targets.length; index++)
			{
				if (!(targets[index] instanceof L2Character))
				{
					continue;
				}
				
				L2Character target = (L2Character) targets[index];
				
				if (target instanceof L2PcInstance)
				{
					L2PcInstance targetChar = (L2PcInstance) target;
					
					// Check to see if the current reader target is in a festival.
					if (targetChar.isFestivalParticipant())
					{
						targetChar.sendPacket(SystemMessage.sendString("You may not use an escape skill in a festival."));
						continue;
					}
					
					// Check to see if reader is in jail
					if (targetChar.isInJail())
					{
						targetChar.sendPacket(SystemMessage.sendString("You can not escape from jail."));
						continue;
					}
					
					// Check to see if reader is in a duel
					if (targetChar.isInDuel())
					{
						targetChar.sendPacket(SystemMessage.sendString("You cannot use escape skills during a duel."));
						continue;
					}
				}
				
				target.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
		}
		catch (Throwable e)
		{
			if (Config.DEBUG)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}