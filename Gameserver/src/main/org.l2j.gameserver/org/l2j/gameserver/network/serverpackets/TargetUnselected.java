package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Character;

/**
 * format dddd sample 0000: 3a 69 08 10 48 02 c1 00 00 f7 56 00 00 89 ea ff :i..H.....V..... 0010: ff 0c b2 d8 61 ....a
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class TargetUnselected extends L2GameServerPacket
{
	private final int _targetObjId;
	private final int _x;
	private final int _y;
	private final int _z;

	public TargetUnselected(L2Character character)
	{
		_targetObjId = character.getObjectId();
		_x = character.getX();
		_y = character.getY();
		_z = character.getZ();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x2a);
		writeInt(_targetObjId);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		// writeInt(_target.getTargetId()); //?? probably not used in client
	}
}
