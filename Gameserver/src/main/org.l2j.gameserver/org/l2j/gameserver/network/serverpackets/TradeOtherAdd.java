package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeList;

public class TradeOtherAdd extends L2GameServerPacket
{
	private final TradeList.TradeItem _item;
	
	public TradeOtherAdd(TradeList.TradeItem item)
	{
		_item = item;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x21);
		
		writeShort(1); // item count
		
		writeShort(_item.getItem().getType().ordinal()); // item type1
		writeInt(_item.getObjectId());
		writeInt(_item.getItem().getId());
		writeLong(_item.getCount());
		writeShort(_item.getItem().getCommissionType().ordinal()); // item type2
		writeShort(0x00); // ?
		
		writeInt(0); // todo _item.getItem().getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
		writeShort(_item.getEnchant()); // enchant level
		writeShort(0x00); // ?
		writeShort(0x00);
	}
}
