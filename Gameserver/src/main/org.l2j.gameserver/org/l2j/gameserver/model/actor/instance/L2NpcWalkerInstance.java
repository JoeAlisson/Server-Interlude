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

import org.l2j.commons.Config;
import org.l2j.gameserver.ai.AI;
import org.l2j.gameserver.ai.L2NpcWalkerAI;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.serverpackets.CreatureSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * This class manages some npcs can walk in the city. <br>
 * It inherits allTemplates methods from L2NpcInstance. <br>
 * <br>
 * @original author Rayan RPG for L2Emu Project
 * @since 819
 */
public class L2NpcWalkerInstance extends L2NpcInstance
{
	private static final Logger logger = LoggerFactory.getLogger(L2NpcWalkerInstance.class);

	/**
	 * Constructor of L2NpcWalkerInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <BR>
	 * @param objectId
	 * @param template
	 */
	public L2NpcWalkerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setAI(new L2NpcWalkerAI(new L2NpcWalkerAIAccessor()));
	}
	
	/**
	 * AI can't be deattached, npc must move always with the same AI instance.
	 * @param newAI AI to set for this L2NpcWalkerInstance
	 */
	@Override
	public void setAI(AI newAI)
	{
		if (ai == null)
		{
			super.setAI(newAI);
		}
	}
	
	@Override
	public void onSpawn()
	{
		
		((L2NpcWalkerAI) getAI()).setHomeX(getX());
		((L2NpcWalkerAI) getAI()).setHomeY(getY());
		((L2NpcWalkerAI) getAI()).setHomeZ(getZ());
	}
	
	/**
	 * Sends a chat to allTemplates _knowObjects
	 * @param chat message to say
	 */
	public void broadcastChat(String chat)
	{
		Map<Integer, L2PcInstance> _knownPlayers = getKnownList().getKnownPlayers();
		
		if (_knownPlayers == null)
		{
			if (Config.DEVELOPER)
			{
				logger.info("broadcastChat _players == null");
			}
			return;
		}
		
		// we send message to known players only!
		if (_knownPlayers.size() > 0)
		{
			CreatureSay cs = new CreatureSay(getObjectId(), 0, getName(), chat);
			
			// we interact and list players here
			for (L2PcInstance players : _knownPlayers.values())
			{
				// finally send packet :D
				players.sendPacket(cs);
			}
		}
	}
	
	/**
	 * NPCs are immortal
	 * @param i ignore it
	 * @param attacker ignore it
	 * @param awake ignore it
	 */
	@Override
	public void reduceCurrentHp(double i, L2Character attacker, boolean awake)
	{
	}
	
	/**
	 * NPCs are immortal
	 * @param killer ignore it
	 * @return false
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		return false;
	}
	
	public class L2NpcWalkerAIAccessor extends L2Character.AIAccessor {

        @Override
        public L2NpcWalkerInstance getActor() {
            return L2NpcWalkerInstance.this;
        }

        @Override
		public void detachAI()
		{
		}
	}
}
