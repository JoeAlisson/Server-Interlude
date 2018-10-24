package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2BoatInstance;

/**
 * @author Maktakien
 */
public class VehicleDeparture extends L2GameServerPacket
{
	private final L2BoatInstance _boat;
	private final int _speed1;
	private final int _speed2;// rotation
	private final int _x;
	private final int _y;
	private final int _z;

	public VehicleDeparture(L2BoatInstance boat, int speed1, int speed2, int x, int y, int z)
	{
		_boat = boat;
		_speed1 = speed1;
		_speed2 = speed2;
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0x5a);
		writeInt(_boat.getObjectId());
		writeInt(_speed1);
		writeInt(_speed2);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		
	}
}
