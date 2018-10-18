package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

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
public class MoveOnVehicle extends L2GameServerPacket
{
	private static final String _S__71_MOVEONVEICLE = "[S] 71 MoveOnVehicle";
	private final int _id;
	private final int _x, _y, _z;
	private final L2PcInstance _activeChar;
	
	public MoveOnVehicle(int vehicleID, L2PcInstance player, int x, int y, int z)
	{
		_id = vehicleID;
		_activeChar = player;
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x71);
		
		writeInt(_activeChar.getObjectId());
		writeInt(_id);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_activeChar.getX());
		writeInt(_activeChar.getY());
		writeInt(_activeChar.getZ());
	}
}