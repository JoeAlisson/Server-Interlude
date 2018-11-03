package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ExConfirmVariationRefiner;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;

/**
 * Fromat(ch) dd
 * @author -Wooden-
 */
public class RequestConfirmRefinerItem extends L2GameClientPacket
{
	private static final String _C__D0_2A_REQUESTCONFIRMREFINERITEM = "[C] D0:2A RequestConfirmRefinerItem";
	
	private static final int GEMSTONE_D = 2130;
	private static final int GEMSTONE_C = 2131;
	
	private int _targetItemObjId;
	private int _refinerItemObjId;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readInt();
		_refinerItemObjId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		L2ItemInstance targetItem = (L2ItemInstance) L2World.getInstance().findObject(_targetItemObjId);
		L2ItemInstance refinerItem = (L2ItemInstance) L2World.getInstance().findObject(_refinerItemObjId);
		
		if ((targetItem == null) || (refinerItem == null))
		{
			return;
		}
		

		int refinerItemId = refinerItem.getItem().getId();
		
		// is the item a life stone?
		if ((refinerItemId < 8723) || (refinerItemId > 8762))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
			return;
		}

		CrystalType itemGrade = targetItem.getItem().getCrystalInfo().getType();
		
		int gemstoneCount = 0;
		int gemstoneItemId = 0;
		int lifeStoneLevel = getLifeStoneLevel(refinerItemId);
		SystemMessage sm = new SystemMessage(SystemMessageId.REQUIRES_S1_S2);
		switch (itemGrade)
		{
			case C:
				gemstoneCount = 20;
				gemstoneItemId = GEMSTONE_D;
				sm.addNumber(gemstoneCount);
				sm.addString("Gemstone D");
				break;
			case B:
				if (lifeStoneLevel < 3)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
					return;
				}
				gemstoneCount = 30;
				gemstoneItemId = GEMSTONE_D;
				sm.addNumber(gemstoneCount);
				sm.addString("Gemstone D");
				break;
			case A:
				if (lifeStoneLevel < 6)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
					return;
				}
				gemstoneCount = 20;
				gemstoneItemId = GEMSTONE_C;
				sm.addNumber(gemstoneCount);
				sm.addString("Gemstone C");
				break;
			case S:
				if (lifeStoneLevel != 10)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
					return;
				}
				gemstoneCount = 25;
				gemstoneItemId = GEMSTONE_C;
				sm.addNumber(gemstoneCount);
				sm.addString("Gemstone C");
				break;
		}
		
		activeChar.sendPacket(new ExConfirmVariationRefiner(_refinerItemObjId, refinerItemId, gemstoneItemId, gemstoneCount));
		
		activeChar.sendPacket(sm);
	}
	
	private int getLifeStoneGrade(int itemId)
	{
		itemId -= 8723;
		if (itemId < 10)
		{
			return 0; // normal grade
		}
		if (itemId < 20)
		{
			return 1; // mid grade
		}
		if (itemId < 30)
		{
			return 2; // high grade
		}
		return 3; // top grade
	}
	
	private int getLifeStoneLevel(int itemId)
	{
		itemId -= 10 * getLifeStoneGrade(itemId);
		itemId -= 8722;
		return itemId;
	}
	
	@Override
	public String getType()
	{
		return _C__D0_2A_REQUESTCONFIRMREFINERITEM;
	}
}
