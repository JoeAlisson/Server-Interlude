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
package org.l2j.gameserver.handler.itemhandlers;

import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import org.l2j.gameserver.model.entity.database.Weapon;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.MagicSkillUser;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.CrystalType;
import org.l2j.gameserver.templates.ItemType;
import org.l2j.gameserver.util.Broadcast;


/**
 * @author -Nemesiss-
 */
public class FishShots implements IItemHandler
{
	// All the item IDs that this handler knows.
	private static final int[] ITEM_IDS =
	{
		6535,
		6536,
		6537,
		6538,
		6539,
		6540
	};
	private static final int[] SKILL_IDS =
	{
		2181,
		2182,
		2183,
		2184,
		2185,
		2186
	};
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IItemHandler#useItem(org.l2j.gameserver.model.L2PcInstance, org.l2j.gameserver.model.L2ItemInstance)
	 */
	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		Weapon weaponItem = activeChar.getActiveWeaponItem();
		
		if ((weaponInst == null) || (weaponItem.getType() != ItemType.ROD))
		{
			return;
		}
		
		if (weaponInst.getChargedFishshot())
		{
			// spiritshot is already active
			return;
		}
		
		int FishshotId = item.getItemId();
		CrystalType grade = weaponItem.getCrystalType();
		int count = item.getCount();
		
		if (((grade == CrystalType.NONE) && (FishshotId != 6535)) || ((grade == CrystalType.D) && (FishshotId != 6536)) || ((grade == CrystalType.C) && (FishshotId != 6537)) || ((grade == CrystalType.B) && (FishshotId != 6538)) || ((grade == CrystalType.A) && (FishshotId != 6539)) || ((grade == CrystalType.S) && (FishshotId != 6540)))
		{
			// 1479 - This fishing shot is not fit for the fishing pole crystal.
			activeChar.sendPacket(new SystemMessage(SystemMessageId.WRONG_FISHINGSHOT_GRADE));
			return;
		}
		
		if (count < 1)
		{
			return;
		}
		
		weaponInst.setChargedFishshot(true);
		activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false);
		L2Object oldTarget = activeChar.getTarget();
		activeChar.setTarget(activeChar);
		
		// activeChar.sendPacket(new SystemMessage(SystemMessage.ENABLED_SPIRITSHOT));
		
		MagicSkillUser MSU = new MagicSkillUser(activeChar, SKILL_IDS[grade.ordinal()], 1, 0, 0);
		Broadcast.toSelfAndKnownPlayers(activeChar, MSU);
		activeChar.setTarget(oldTarget);
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
