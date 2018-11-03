package org.l2j.gameserver.handler.itemhandlers;

import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.serverpackets.MagicSkillUser;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;
import org.l2j.gameserver.templates.xml.jaxb.Weapon;
import org.l2j.gameserver.util.Broadcast;

public class BlessedSpiritShot implements IItemHandler
{
	// allTemplates the items ids that this handler knowns
	private static final int[] ITEM_IDS =
	{
		3947,
		3948,
		3949,
		3950,
		3951,
		3952
	};
	//TODO move skills to CrystalType
	private static final int[] SKILL_IDS =
	{
		2061,
		2160,
		2161,
		2162,
		2163,
		2164
	};
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IItemHandler#useItem(org.l2j.gameserver.model.L2PcInstance, org.l2j.gameserver.model.L2ItemInstance)
	 */
	@Override
	public synchronized void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		Weapon weaponItem = activeChar.getActiveWeaponItem();
		int itemId = item.getItemId();
		
		if (activeChar.isInOlympiadMode())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			sm.addString(item.getItemName());
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		
		// Check if Blessed Spiritshot can be used
		if ((weaponInst == null) || (weaponItem.getShots() == 0))
		{
			if (!activeChar.getAutoSoulShot().containsKey(itemId))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_SPIRITSHOTS));
			}
			return;
		}
		
		// Check if Blessed Spiritshot is already active (it can be charged over Spiritshot)
		if (weaponInst.getChargedSpiritshot() != L2ItemInstance.CHARGED_NONE)
		{
			return;
		}
		
		// Check for correct grade
		CrystalType weaponGrade = weaponItem.getCrystalInfo().getType();
		if (((weaponGrade == CrystalType.NONE) && (itemId != 3947)) || ((weaponGrade == CrystalType.D) && (itemId != 3948)) || ((weaponGrade == CrystalType.C) && (itemId != 3949)) || ((weaponGrade == CrystalType.B) && (itemId != 3950)) || ((weaponGrade == CrystalType.A) && (itemId != 3951)) || ((weaponGrade == CrystalType.S) && (itemId != 3952)))
		{
			if (!activeChar.getAutoSoulShot().containsKey(itemId))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH));
			}
			return;
		}
		
		// Consume Blessed Spiritshot if reader has enough of them
		if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), weaponItem.getShots(), null, false))
		{
			if (activeChar.getAutoSoulShot().containsKey(itemId))
			{
				activeChar.removeAutoSoulShot(itemId);
				activeChar.sendPacket(new ExAutoSoulShot(itemId, 0));
				
				SystemMessage sm = new SystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED);
				sm.addString(item.getItem().getName());
				activeChar.sendPacket(sm);
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS));
			}
			return;
		}
		
		// Charge Blessed Spiritshot
		weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
		
		// Send message to client
		activeChar.sendPacket(new SystemMessage(SystemMessageId.ENABLED_SPIRITSHOT));
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUser(activeChar, activeChar, SKILL_IDS[weaponGrade.ordinal()], 1, 0, 0), 360000/* 600 */);
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
