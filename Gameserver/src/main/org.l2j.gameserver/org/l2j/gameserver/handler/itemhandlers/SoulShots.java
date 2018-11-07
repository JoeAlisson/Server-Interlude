package org.l2j.gameserver.handler.itemhandlers;

import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.serverpackets.MagicSkillUser;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.skills.Stats;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;
import org.l2j.gameserver.templates.xml.jaxb.Weapon;
import org.l2j.gameserver.util.Broadcast;

import static java.util.Objects.isNull;

public class SoulShots implements IItemHandler
{
	// All the item IDs that this handler knows.
	private static final int[] ITEM_IDS =
	{
		5789,
		1835,
		1463,
		1464,
		1465,
		1466,
		1467
	};
	private static final int[] SKILL_IDS =
	{
		2039,
		2150,
		2151,
		2152,
		2153,
		2154
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
		int itemId = item.getId();
		
		// Check if Soulshot can be used
		if ((isNull(weaponInst)) || (weaponInst.getShots() == 0)) {
			if (!activeChar.getAutoSoulShot().containsKey(itemId))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_SOULSHOTS));
			}
			return;
		}
		
		// Check for correct grade
		CrystalType weaponGrade = weaponInst.getCrystal();
		if (((weaponGrade == CrystalType.NONE) && (itemId != 5789) && (itemId != 1835)) || ((weaponGrade == CrystalType.D) && (itemId != 1463)) || ((weaponGrade == CrystalType.C) && (itemId != 1464)) || ((weaponGrade == CrystalType.B) && (itemId != 1465)) || ((weaponGrade == CrystalType.A) && (itemId != 1466)) || ((weaponGrade == CrystalType.S) && (itemId != 1467)))
		{
			if (!activeChar.getAutoSoulShot().containsKey(itemId))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.SOULSHOTS_GRADE_MISMATCH));
			}
			return;
		}
		
		activeChar.soulShotLock.lock();
		try
		{
			// Check if Soulshot is already active
			if (weaponInst.getChargedSoulshot() != L2ItemInstance.CHARGED_NONE)
			{
				return;
			}
			
			// Consume Soulshots if reader has enough of them
			int saSSCount = (int) activeChar.getStat().calcStat(Stats.SOULSHOT_COUNT, 0, null, null);
			int SSCount = saSSCount == 0 ? weaponInst.getShots() : saSSCount;
			
			if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), SSCount, null, false))
			{
				if (activeChar.getAutoSoulShot().containsKey(itemId))
				{
					activeChar.removeAutoSoulShot(itemId);
					activeChar.sendPacket(new ExAutoSoulShot(itemId, 0));
					
					SystemMessage sm = new SystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED);
					sm.addString(item.getName());
					activeChar.sendPacket(sm);
				}
				else
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_SOULSHOTS));
				}
				return;
			}
			
			// Charge soulshot
			weaponInst.setChargedSoulshot(L2ItemInstance.CHARGED_SOULSHOT);
		}
		finally
		{
			activeChar.soulShotLock.unlock();
		}
		
		// Send message to client
		activeChar.sendPacket(new SystemMessage(SystemMessageId.ENABLED_SOULSHOT));
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUser(activeChar, activeChar, SKILL_IDS[weaponGrade.ordinal()], 1, 0, 0), 360000/* 600 */);
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
