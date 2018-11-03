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
package org.l2j.gameserver.handler.usercommandhandlers;

import org.l2j.commons.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.datatables.MapRegionTable;
import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.TvTEvent;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.MagicSkillUser;
import org.l2j.gameserver.serverpackets.SetupGauge;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Broadcast;


/**
 *
 *
 */
public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};
	private static final int REQUIRED_LEVEL = Config.GM_ESCAPE;
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IUserCommandHandler#useUserCommand(int, org.l2j.gameserver.model.L2PcInstance)
	 */
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		// Thanks nbd
		if (!TvTEvent.onEscapeUse(activeChar.getName()))
		{
			activeChar.sendPacket(new ActionFailed());
			return false;
		}
		
		if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || activeChar.isAlikeDead() || activeChar.isInOlympiadMode())
		{
			return false;
		}
		
		int unstuckTimer = (activeChar.getAccessLevel() >= REQUIRED_LEVEL ? 5000 : Config.UNSTUCK_INTERVAL * 1000);
		
		// Check to see if the reader is in a festival.
		if (activeChar.isFestivalParticipant())
		{
			activeChar.sendMessage("You may not use an escape command in a festival.");
			return false;
		}
		
		// Check to see if reader is in jail
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You can not escape from jail.");
			return false;
		}
		
		SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString("After " + (unstuckTimer / 60000) + " min. you be returned to near village.");
		
		activeChar.getAI().setIntention(Intention.AI_INTENTION_IDLE);
		// SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();
		
		MagicSkillUser msk = new MagicSkillUser(activeChar, 1050, 1, unstuckTimer, 0);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 810000/* 900 */);
		SetupGauge sg = new SetupGauge(0, unstuckTimer);
		activeChar.sendPacket(sg);
		// End SoE Animation section
		
		EscapeFinalizer ef = new EscapeFinalizer(activeChar);
		// continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, unstuckTimer));
		activeChar.setSkillCastEndTime(10 + GameTimeController.getGameTicks() + (unstuckTimer / GameTimeController.MILLIS_IN_TICK));
		
		return true;
	}
	
	static class EscapeFinalizer implements Runnable
	{
		private final L2PcInstance _activeChar;
		
		EscapeFinalizer(L2PcInstance activeChar)
		{
			_activeChar = activeChar;
		}
		
		@Override
		public void run()
		{
			if (_activeChar.isDead())
			{
				return;
			}
			
			_activeChar.setIsIn7sDungeon(false);
			
			_activeChar.enableAllSkills();
			
			try
			{
				_activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
			catch (Throwable e)
			{
				if (Config.DEBUG)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IUserCommandHandler#getUserCommandList()
	 */
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
