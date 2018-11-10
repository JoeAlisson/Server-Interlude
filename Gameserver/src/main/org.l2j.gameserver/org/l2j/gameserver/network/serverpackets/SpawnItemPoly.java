package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Object;

/**
 * 15 ee cc 11 43 object id 39 00 00 00 item id 8f 14 00 00 x b7 f1 00 00 y 60 f2 ff ff z 01 00 00 00 show item count 7a 00 00 00 count . format dddddddd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class SpawnItemPoly extends L2GameServerPacket
{
	private int _objectId;
	private int _itemId;
	private int _x, _y, _z;
	private int _stackable;
	private long _count;
	
	public SpawnItemPoly(L2Object object)
	{
		if (object instanceof L2ItemInstance)
		{
			L2ItemInstance item = (L2ItemInstance) object;
			_objectId = object.getObjectId();
			_itemId = object.getPolyMorph();
			_x = item.getX();
			_y = item.getY();
			_z = item.getZ();
			_stackable = item.isStackable() ? 0x01 : 0x00;
			_count = item.getCount();
		}
		else
		{
			_objectId = object.getObjectId();
			_itemId = object.getPolyMorph();
			_x = object.getX();
			_y = object.getY();
			_z = object.getZ();
			_stackable = 0x00;
			_count = 1;
		}
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
