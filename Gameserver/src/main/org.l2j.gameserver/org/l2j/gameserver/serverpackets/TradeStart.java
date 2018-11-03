package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class TradeStart extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final L2ItemInstance[] _itemList;
	
	public TradeStart(L2PcInstance player)
	{
		_activeChar = player;
		_itemList = _activeChar.getInventory().getAvailableItems(true);
	}
	
	@Override
	protected final void writeImpl()
	{// 0x2e TradeStart d h (h dddhh dhhh)
		if ((_activeChar.getActiveTradeList() == null) || (_activeChar.getActiveTradeList().getPartner() == null))
		{
			return;
		}
		
		writeByte(0x1E);
		writeInt(_activeChar.getActiveTradeList().getPartner().getObjectId());
		// writeInt((_activeChar != null || _activeChar.getTransactionRequester() != null)? _activeChar.getTransactionRequester().getObjectId() : 0);
		
		writeShort(_itemList.length);
		for (L2ItemInstance item : _itemList)// int i = 0; i < count; i++)
		{
			writeShort(item.getItem().getType().ordinal()); // item type1
			writeInt(item.getObjectId());
			writeInt(item.getItemId());
			writeLong(item.getCount());
			writeShort(item.getItem().getCommissionType().ordinal()); // item type2
			writeShort(0x00); // ?
			
			writeInt(0);// TODO item.getItem().getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeShort(item.getEnchantLevel()); // enchant level
			writeShort(0x00); // ?
			writeShort(0x00);
		}
	}
}
