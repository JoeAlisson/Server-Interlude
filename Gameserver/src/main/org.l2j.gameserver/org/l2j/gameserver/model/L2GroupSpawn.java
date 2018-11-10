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
package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Territory;
import org.l2j.gameserver.factory.IdFactory;
import org.l2j.gameserver.model.actor.instance.L2ControllableMobInstance;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.entity.database.NpcTemplate;

import java.lang.reflect.Constructor;

import static org.l2j.gameserver.templates.base.NpcType.L2Minion;
import static org.l2j.gameserver.templates.base.NpcType.L2Pet;

/**
 * @author littlecrow A special spawn implementation to spawn controllable mob
 */
class L2GroupSpawn extends L2Spawn {
	private final Constructor<?> _constructor;
	private final NpcTemplate _template;
	
	L2GroupSpawn(NpcTemplate mobTemplate) throws SecurityException, NoSuchMethodException
	{
		super(mobTemplate);
		_constructor = L2ControllableMobInstance.class.getConstructor(int.class, NpcTemplate.class);
		_template = mobTemplate;
		
		setAmount(1);
	}
	
	L2NpcInstance doGroupSpawn()
	{
		L2NpcInstance mob;
		
		try
		{
			if (L2Pet.equals(_template.getType()) || L2Minion.equals(_template.getType()))
			{
				return null;
			}
			
			Object[] parameters =
			{
				IdFactory.getInstance().getNextId(),
				_template
			};
			Object tmp = _constructor.newInstance(parameters);
			
			if (!(tmp instanceof L2NpcInstance))
			{
				return null;
			}
			
			mob = (L2NpcInstance) tmp;
			
			int newlocx, newlocy, newlocz;
			
			if ((getLocx() == 0) && (getLocy() == 0))
			{
				if (getLocation() == 0)
				{
					return null;
				}
				
				int p[] = Territory.getInstance().getRandomPoint(getLocation());
				newlocx = p[0];
				newlocy = p[1];
				newlocz = p[2];
			}
			else
			{
				newlocx = getLocx();
				newlocy = getLocy();
				newlocz = getLocz();
			}
			
			mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());
			
			if (getHeading() == -1)
			{
				mob.setHeading(Rnd.nextInt(61794));
			}
			else
			{
				mob.setHeading(getHeading());
			}
			
			mob.setSpawn(this);
			mob.spawnMe(newlocx, newlocy, newlocz);
			mob.onSpawn();
			
			if (Config.DEBUG)
			{
				_log.debug("spawned Mob ID: " + _template.getId() + " ,at: " + mob.getX() + " x, " + mob.getY() + " y, " + mob.getZ() + " z");
			}
			
			return mob;
			
		}
		catch (Exception e)
		{
			_log.warn("NPC class not found: " + e);
			return null;
		}
	}
}