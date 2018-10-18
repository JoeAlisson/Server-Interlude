package com.l2jbr.gameserver.serverpackets;

/**
 * format ddddd sample 0000: 39 0b 07 10 48 3e 31 10 48 3a f6 00 00 91 5b 00 9...H>1.H:....[. 0010: 00 4c f1 ff ff .L...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class TargetSelected extends L2GameServerPacket
{
	private final int _objectId;
	private final int _targetObjId;
	private final int _x;
	private final int _y;
	private final int _z;

	public TargetSelected(int objectId, int targetId, int x, int y, int z)
	{
		_objectId = objectId;
		_targetObjId = targetId;
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x29);
		writeInt(_objectId);
		writeInt(_targetObjId);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
	}
}
