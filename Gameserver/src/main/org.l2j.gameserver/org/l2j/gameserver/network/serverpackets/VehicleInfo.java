package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2BoatInstance;

/**
 * @author Maktakien
 */
public class VehicleInfo extends L2GameServerPacket
{
	private final L2BoatInstance _boat;

	public VehicleInfo(L2BoatInstance boat)
	{
		_boat = boat;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0x59);
		writeInt(_boat.getObjectId());
		writeInt(_boat.getX());
		writeInt(_boat.getY());
		writeInt(_boat.getZ());
		writeInt(_boat.getPosition().getHeading());
		
	}
}
