package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;

public class DropItem extends L2GameServerPacket {
	private final L2ItemInstance _item;
	private final int _charObjId;
	
	public DropItem(L2ItemInstance item, int playerObjId)
	{
		_item = item;
		_charObjId = playerObjId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x0c);
		writeInt(_charObjId);
		writeInt(_item.getObjectId());
		writeInt(_item.getItemId());
		
		writeInt(_item.getX());
		writeInt(_item.getY());
		writeInt(_item.getZ());
		// only show item count if it is a stackable item
		if (_item.isStackable())
		{
			writeInt(0x01);
		}
		else
		{
			writeInt(0x00);
		}
		writeLong(_item.getCount());
		
		writeInt(1); // unknown
	}
	
}
