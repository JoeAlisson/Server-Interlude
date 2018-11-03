package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ExConfirmVariationGemstone;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;

/**
 * Format:(ch) dddd
 * @author -Wooden-
 */
public final class RequestConfirmGemStone extends L2GameClientPacket
{
	private static final String _C__D0_2B_REQUESTCONFIRMGEMSTONE = "[C] D0:2B RequestConfirmGemStone";
	private int _targetItemObjId;
	private int _refinerItemObjId;
	private int _gemstoneItemObjId;
	private int _gemstoneCount;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readInt();
		_refinerItemObjId = readInt();
		_gemstoneItemObjId = readInt();
		_gemstoneCount = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		L2ItemInstance targetItem = (L2ItemInstance) L2World.getInstance().findObject(_targetItemObjId);
		L2ItemInstance refinerItem = (L2ItemInstance) L2World.getInstance().findObject(_refinerItemObjId);
		L2ItemInstance gemstoneItem = (L2ItemInstance) L2World.getInstance().findObject(_gemstoneItemObjId);
		
		if ((targetItem == null) || (refinerItem == null) || (gemstoneItem == null))
		{
			return;
		}
		
		// Make sure the item is a gemstone
		int gemstoneItemId = gemstoneItem.getItem().getId();
		if ((gemstoneItemId != 2130) && (gemstoneItemId != 2131))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
			return;
		}
		
		// Check if the gemstoneCount is sufficant
		CrystalType itemGrade = targetItem.getItem().getCrystalInfo().getType();
		switch (itemGrade)
		{
			case C:
				if ((_gemstoneCount != 20) || (gemstoneItemId != 2130))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT));
					return;
				}
				break;
			case B:
				if ((_gemstoneCount != 30) || (gemstoneItemId != 2130))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT));
					return;
				}
				break;
			case A:
				if ((_gemstoneCount != 20) || (gemstoneItemId != 2131))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT));
					return;
				}
				break;
			case S:
				if ((_gemstoneCount != 25) || (gemstoneItemId != 2131))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT));
					return;
				}
				break;
		}
		
		activeChar.sendPacket(new ExConfirmVariationGemstone(_gemstoneItemObjId, _gemstoneCount));
		activeChar.sendPacket(new SystemMessage(SystemMessageId.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN));
	}
	
	@Override
	public String getType()
	{
		return _C__D0_2B_REQUESTCONFIRMGEMSTONE;
	}
}
