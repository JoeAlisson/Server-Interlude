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
package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.datatables.SkillTable;
import com.l2jbr.gameserver.model.L2Skill;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 * @version $Revision: 1.7.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestMagicSkillUse extends L2GameClientPacket
{
	private static final String _C__2F_REQUESTMAGICSKILLUSE = "[C] 2F RequestMagicSkillUse";
	private static Logger _log = LoggerFactory.getLogger(RequestMagicSkillUse.class.getName());
	
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_magicId = readInt(); // Identifier of the used skill
		_ctrlPressed = readInt() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = readUnsignedByte() != 0; // True if Shift pressed
	}
	
	@Override
	protected void runImpl()
	{
		// Get the current L2PcInstance of the player
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		// Get the level of the used skill
		int level = activeChar.getSkillLevel(_magicId);
		if (level <= 0)
		{
			activeChar.sendPacket(new ActionFailed());
			return;
		}
		
		if (activeChar.isOutOfControl())
		{
			activeChar.sendPacket(new ActionFailed());
			return;
		}
		
		// Get the L2Skill template corresponding to the skillID received from the client
		L2Skill skill = SkillTable.getInstance().getInfo(_magicId, level);
		
		// Check the validity of the skill
		if (skill != null)
		{
			// _log.debug("	skill:"+skill.getName() + " level:"+skill.getLevel() + " passive:"+skill.isPassive());
			// _log.debug("	range:"+skill.getCastRange()+" targettype:"+skill.getTargetType()+" optype:"+skill.getOperateType()+" power:"+skill.getPower());
			// _log.debug("	reusedelay:"+skill.getReuseDelay()+" hittime:"+skill.getHitTime());
			// _log.debug("	currentState:"+activeChar.getCurrentState()); //for debug
			
			// If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
			if ((skill.getSkillType() == L2Skill.SkillType.RECALL) && !Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (activeChar.getKarma() > 0))
			{
				return;
			}
			
			// activeChar.stopMove();
			activeChar.useMagic(skill, _ctrlPressed, _shiftPressed);
		}
		else
		{
			activeChar.sendPacket(new ActionFailed());
			_log.warn("No skill found!!");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__2F_REQUESTMAGICSKILLUSE;
	}
}
