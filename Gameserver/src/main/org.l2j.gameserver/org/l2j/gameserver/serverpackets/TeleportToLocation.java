package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Object;

/**
 * format dddd sample 0000: 3a 69 08 10 48 02 c1 00 00 f7 56 00 00 89 ea ff :i..H.....V..... 0010: ff 0c b2 d8 61 ....a
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class TeleportToLocation extends L2GameServerPacket
{
	private final int _targetObjId;
	private final int _x;
	private final int _y;
	private final int _z;

	public TeleportToLocation(L2Object obj, int x, int y, int z)
	{
		_targetObjId = obj.getObjectId();
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x28);
		writeInt(_targetObjId);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
	}
}
