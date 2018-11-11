package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExConfirmVariationItem;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.SubType;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;

/**
 * Format:(ch) d
 * @author -Wooden-
 */
public final class RequestConfirmTargetItem extends L2GameClientPacket
{
	private static final String _C__D0_29_REQUESTCONFIRMTARGETITEM = "[C] D0:29 RequestConfirmTargetItem";
	private int _itemObjId;
	
	@Override
	protected void readImpl()
	{
		_itemObjId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		L2ItemInstance item = (L2ItemInstance) L2World.getInstance().findObject(_itemObjId);
		
		if (item == null)
		{
			return;
		}
		
		if (activeChar.getLevel() < 46)
		{
			activeChar.sendMessage("You have to be level 46 in order to augment an item");
			return;
		}
		
		// check if the item is augmentable
		CrystalType itemGrade = item.getCrystal();
		SubType itemType = item.getCommissionType();

		if (item.isAugmented())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN));
			return;
		}
		// TODO: can do better? : currently: using isdestroyable() as a check for hero / cursed weapons
		else if ((itemGrade.compareTo(CrystalType.C) < 0) || !(item.isWeapon()) || !item.isDestroyable() || item.isShadowItem())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
			return;
		}
		
		// check if the reader can augment
		if (activeChar.getPrivateStoreType() != L2PcInstance.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION));
			return;
		}
		if (activeChar.isDead())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD));
			return;
		}
		if (activeChar.isParalyzed())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED));
			return;
		}
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING));
			return;
		}
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN));
			return;
		}
		
		activeChar.sendPacket(new ExConfirmVariationItem(_itemObjId));
		activeChar.sendPacket(new SystemMessage(SystemMessageId.SELECT_THE_CATALYST_FOR_AUGMENTATION));
	}
	
	@Override
	public String getType()
	{
		return _C__D0_29_REQUESTCONFIRMTARGETITEM;
	}
}
