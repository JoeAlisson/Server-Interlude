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

import org.l2j.gameserver.model.L2Skill;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.model.zone.Zone;


public class L2SiegeSummonInstance extends L2SummonInstance
{
	public static final int SIEGE_GOLEM_ID = 14737;
	public static final int HOG_CANNON_ID = 14768;
	public static final int SWOOP_CANNON_ID = 14839;
	
	public L2SiegeSummonInstance(int objectId, NpcTemplate template, L2PcInstance owner, L2Skill skill)
	{
		super(objectId, template, owner, skill);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		if (!getOwner().isGM() && !isInsideZone(Zone.SIEGE))
		{
			unSummon(getOwner());
			getOwner().sendMessage("Summon was unsummoned because it exited siege zone");
		}
	}
}
