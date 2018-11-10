package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Character;

/**
 * 0000: 75 7a 07 80 49 63 27 00 4a ea 01 00 00 c1 37 fe uz..Ic'.J.....7.
 * <p>
 * 0010: ff 9e c3 03 00 8f f3 ff ff .........
 * <p>
 * <p>
 * format dddddd (reader id, target id, distance, startx, starty, startz)
 * <p>
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/04/06 16:13:46 $
 */
public class MoveToPawn extends L2GameServerPacket
{
	private static final String _S__75_MOVETOPAWN = "[S] 60 MoveToPawn";
	private final int _charObjId;
	private final int _targetId;
	private final int _distance;
	private final int _x, _y, _z;
	
	public MoveToPawn(L2Character cha, L2Character target, int distance)
	{
		_charObjId = cha.getObjectId();
		_targetId = target.getObjectId();
		_distance = distance;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x60);
		
		writeInt(_charObjId);
		writeInt(_targetId);
		writeInt(_distance);
		
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
	}
}
