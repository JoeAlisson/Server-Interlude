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
package org.l2j.gameserver.model.actor.knownlist;

import org.l2j.gameserver.ai.Event;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;


public class MonsterKnownList extends AttackableKnownList
{
	// =========================================================
	// Data Field
	
	// =========================================================
	// Constructor
	public MonsterKnownList(L2MonsterInstance activeChar)
	{
		super(activeChar);
	}
	
	// =========================================================
	// Method - Public
	@Override
	public boolean addKnownObject(L2Object object)
	{
		return addKnownObject(object, null);
	}
	
	@Override
	public boolean addKnownObject(L2Object object, L2Character dropper)
	{
		if (!super.addKnownObject(object, dropper))
		{
			return false;
		}
		
		// Set the L2MonsterInstance Intention to AI_INTENTION_ACTIVE if the state was AI_INTENTION_IDLE
		if ((object instanceof L2PcInstance) && (getActiveChar().getAI().getIntention() == Intention.AI_INTENTION_IDLE))
		{
			getActiveChar().getAI().setIntention(Intention.AI_INTENTION_ACTIVE, null);
		}
		return true;
	}
	
	@Override
	public boolean removeKnownObject(L2Object object)
	{
		if (!super.removeKnownObject(object))
		{
			return false;
		}
		
		if (!(object instanceof L2Character))
		{
			return true;
		}
		
		if (getActiveChar().hasAI())
		{
			// Notify the L2MonsterInstance AI with EVT_FORGET_OBJECT
			getActiveChar().getAI().notifyEvent(Event.EVT_FORGET_OBJECT, object);
			
			// TODO Remove this function because it's already done in L2Character.removeKnownObject
			// Set the current target to null if the forgotten L2Object was the targeted L2Object
			// L2Character temp = (L2Character)object;
			
			// if (getTarget() == temp)
			// setTarget(null);
		}
		
		if (getActiveChar().isVisible() && getKnownPlayers().isEmpty())
		{
			// Clear the _aggroList of the L2MonsterInstance
			getActiveChar().clearAggroList();
			
			// Remove allTemplates L2Object from _knownObjects and _knownPlayer of the L2MonsterInstance then cancel Attak or Cast and notify AI
			// removeAllKnownObjects();
			
			// TODO Remove this function because it's already done in L2Attackable.removeKnownObject
			// Set the L2MonsterInstance AI to AI_INTENTION_IDLE
			// if (hasAI())
			// getAI().setIntention(Intention.AI_INTENTION_IDLE, null);
		}
		
		return true;
	}
	
	// =========================================================
	// Method - Private
	
	// =========================================================
	// Property - Public
	@Override
	public final L2MonsterInstance getActiveChar()
	{
		return (L2MonsterInstance) super.getActiveChar();
	}
}
