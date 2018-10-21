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
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class SetToLocation extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _x, _y, _z, _heading;
	
	public SetToLocation(L2Character character)
	{
		_charObjId = character.getObjectId();
		_x = character.getX();
		_y = character.getY();
		_z = character.getZ();
		_heading = character.getHeading();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x76);
		
		writeInt(_charObjId);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
	}
}
