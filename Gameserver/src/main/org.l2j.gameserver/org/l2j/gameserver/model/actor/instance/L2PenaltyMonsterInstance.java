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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.Event;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Spawn;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.network.clientpackets.Say2;
import org.l2j.gameserver.network.serverpackets.CreatureSay;


public class L2PenaltyMonsterInstance extends L2MonsterInstance
{
	private L2PcInstance _ptk;
	
	public L2PenaltyMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public L2Character getMostHated()
	{
		return _ptk; // zawsze attakuje tylko 1 osobe chodzby nie wiem co xD
	}
	
	@Deprecated
	public void notifyPlayerDead()
	{
		// Monster kill reader and can by deleted
		deleteMe();
		
		L2Spawn spawn = getSpawn();
		if (spawn != null)
		{
			spawn.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(spawn, false);
		}
	}
	
	public void setPlayerToKill(L2PcInstance ptk)
	{
		if (Rnd.nextInt(100) <= 80)
		{
			CreatureSay cs = new CreatureSay(getObjectId(), Say2.ALL, getName(), "mmm your bait was delicious");
			this.broadcastPacket(cs);
		}
		_ptk = ptk;
		addDamageHate(ptk, 10, 10);
		getAI().notifyEvent(Event.EVT_ATTACKED, ptk);
		addAttackerToAttackByList(ptk);
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (Rnd.nextInt(100) <= 75)
		{
			CreatureSay cs = new CreatureSay(getObjectId(), Say2.ALL, getName(), "I will tell fishes not to take your bait");
			this.broadcastPacket(cs);
		}
		return true;
	}
	
}
