package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;

/**
 * 15 ee cc 11 43 object id 39 00 00 00 item id 8f 14 00 00 x b7 f1 00 00 y 60 f2 ff ff z 01 00 00 00 show item count 7a 00 00 00 count . format dddddddd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class SpawnItem extends L2GameServerPacket
{
	private final int _objectId;
	private final int _itemId;
	private final int _x, _y, _z;
	private final int _stackable;
	private final long _count;
	
	public SpawnItem(L2ItemInstance item)
	{
		_objectId = item.getObjectId();
		_itemId = item.getItemId();
		_x = item.getX();
		_y = item.getY();
		_z = item.getZ();
		_stackable = item.isStackable() ? 0x01 : 0x00;
		_count = item.getCount();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x0b);
		writeInt(_objectId);
		writeInt(_itemId);
		
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		// only show item count if it is a stackable item
		writeInt(_stackable);
		writeLong(_count);
		writeInt(0x00); // c2
	}
}
