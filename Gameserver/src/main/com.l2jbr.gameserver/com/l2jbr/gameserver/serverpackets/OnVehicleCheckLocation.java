package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2BoatInstance;

public class OnVehicleCheckLocation extends L2GameServerPacket
{
	private final L2BoatInstance _boat;
	private final int _x;
	private final int _y;
	private final int _z;

	public OnVehicleCheckLocation(L2BoatInstance instance, int x, int y, int z)
	{
		_boat = instance;
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected void writeImpl()
	{
		
		writeByte(0x5b);
		writeInt(_boat.getObjectId());
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_boat.getPosition().getHeading());
	}
}
