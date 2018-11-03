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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.MyTargetSelected;
import org.l2j.gameserver.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.serverpackets.ValidateLocation;


/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class L2SiegeNpcInstance extends L2FolkInstance
{
	// private static Logger _log = LoggerFactory.getLogger(L2SiegeNpcInstance.class.getName());
	
	public L2SiegeNpcInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}
	
	/**
	 * this is called when a reader interacts with this NPC
	 * @param player
	 */
	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player))
		{
			return;
		}
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance reader
			player.setTarget(this);
			
			// Send a Server->Client packet MyTargetSelected to the L2PcInstance reader
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);
			
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(Intention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				showSiegeInfoWindow(player);
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(new ActionFailed());
	}
	
	/**
	 * If siege is in progress shows the Busy HTML<BR>
	 * else Shows the SiegeInfo window
	 * @param player
	 */
	public void showSiegeInfoWindow(L2PcInstance player)
	{
		if (validateCondition(player))
		{
			getCastle().getSiege().listRegisterClan(player);
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/siege/" + getTemplate().getId() + "-busy.htm");
			html.replace("%castlename%", getCastle().getName());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
			player.sendPacket(new ActionFailed());
		}
	}
	
	private boolean validateCondition(L2PcInstance player)
	{
		if (getCastle().getSiege().getIsInProgress())
		{
			return false; // Busy because of siege
		}
		
		return true;
	}
}