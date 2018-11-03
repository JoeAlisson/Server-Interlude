/* This program is free software; you can redistribute it and/or modify
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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.MyTargetSelected;
import org.l2j.gameserver.serverpackets.StatusUpdate;
import org.l2j.gameserver.serverpackets.ValidateLocation;


public class L2SiegeFlagInstance extends L2NpcInstance
{
	private final L2PcInstance _player;
	private final Siege _siege;
	
	public L2SiegeFlagInstance(L2PcInstance player, int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		_player = player;
		_siege = SiegeManager.getInstance().getSiege(_player.getX(), _player.getY(), _player.getZ());
		if ((_player.getClan() == null) || (_siege == null))
		{
			deleteMe();
		}
		else
		{
			L2SiegeClan sc = _siege.getAttackerClan(_player.getClan());
			if (sc == null)
			{
				deleteMe();
			}
			else
			{
				sc.addFlag(this);
			}
		}
	}
	
	@Override
	public boolean isAttackable()
	{
		// Attackable during siege by attacker only
		return ((getCastle() != null) && (getCastle().getCastleId() > 0) && getCastle().getSiege().getIsInProgress());
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		// Attackable during siege by attacker only
		return ((attacker != null) && (attacker instanceof L2PcInstance) && (getCastle() != null) && (getCastle().getCastleId() > 0) && getCastle().getSiege().getIsInProgress());
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		L2SiegeClan sc = _siege.getAttackerClan(_player.getClan());
		if (sc != null)
		{
			sc.removeFlag(this);
		}
		return true;
	}
	
	@Override
	public void onForcedAttack(L2PcInstance player)
	{
		onAction(player);
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		if ((player == null) || !canTarget(player))
		{
			return;
		}
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance reader
			player.setTarget(this);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance reader
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			
			// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
			StatusUpdate su = new StatusUpdate(getObjectId());
			su.addAttribute(StatusUpdate.CUR_HP, (int) getStatus().getCurrentHp());
			su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
			player.sendPacket(su);
			
			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			if (isAutoAttackable(player) && (Math.abs(player.getZ() - getZ()) < 100))
			{
				player.getAI().setIntention(Intention.AI_INTENTION_ATTACK, this);
			}
			else
			{
				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(new ActionFailed());
			}
		}
	}
}
