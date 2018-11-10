package org.l2j.gameserver.network.serverpackets;

public class Ride extends L2GameServerPacket
{
	public static final int ACTION_MOUNT = 1;
	public static final int ACTION_DISMOUNT = 0;
	private final int _id;
	private final int _bRide;
	private int _rideType;
	private final int _rideClassID;
	
	public Ride(int id, int action, int rideClassId)
	{
		_id = id; // charobjectID
		_bRide = action; // 1 for mount ; 2 for dismount
		_rideClassID = rideClassId + 1000000; // npcID
		
		if ((rideClassId == 12526) || // wind strider
		(rideClassId == 12527) || // star strider
		(rideClassId == 12528)) // twilight strider
		{
			_rideType = 1; // 1 for Strider ; 2 for wyvern
		}
		else if (rideClassId == 12621) // wyvern
		{
			_rideType = 2; // 1 for Strider ; 2 for wyvern
		}
	}
	
	@Override
	public void runImpl()
	{
		
	}
	
	public int getMountType()
	{
		return _rideType;
	}
	
	@Override
	protected final void writeImpl()
	{
		
		writeByte(0x86);
		writeInt(_id);
		writeInt(_bRide);
		writeInt(_rideType);
		writeInt(_rideClassID);
	}
}
