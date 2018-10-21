package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;

/**
 * 0000: 76 7a 07 80 49 ea 01 00 00 c1 37 fe uz..Ic'.J.....7.
 * <p>
 * 0010: ff 9e c3 03 00 8f f3 ff ff .........
 * <p>
 * <p>
 * format dddddd (player id, target id, distance, startx, starty, startz)
 * <p>
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class ValidateLocation extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _x, _y, _z, _heading;
	
	public ValidateLocation(L2Character cha)
	{
		_charObjId = cha.getObjectId();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_heading = cha.getHeading();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x61);
		
		writeInt(_charObjId);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
	}
}
