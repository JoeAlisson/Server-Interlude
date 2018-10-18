package com.l2jbr.gameserver.serverpackets;

public class ExConfirmCancelItem extends L2GameServerPacket
{
	private final int _itemObjId;
	private final int _price;

	public ExConfirmCancelItem(int itemObjId, int price)
	{
		_itemObjId = itemObjId;
		_price = price;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x56);
		writeInt(0x40A97712);
		writeInt(_itemObjId);
		writeInt(0x27);
		writeInt(0x2006);
		writeLong(_price);
		writeInt(0x01);
	}
}
