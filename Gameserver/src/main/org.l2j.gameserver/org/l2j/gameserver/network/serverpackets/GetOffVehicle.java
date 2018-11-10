package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Maktakien
 */
public class GetOffVehicle extends L2GameServerPacket
{
	private final int _x;
	private final int _y;
	private final int _z;
	private final L2PcInstance _activeChar;
	private final L2BoatInstance _boat;

	public GetOffVehicle(L2PcInstance activeChar, L2BoatInstance boat, int x, int y, int z)
	{
		_activeChar = activeChar;
		_boat = boat;
		_x = x;
		_y = y;
		_z = z;
		
		if (_activeChar != null)
		{
			_activeChar.setInBoat(false);
			_activeChar.setBoat(null);
		}
	}
	
	@Override
	protected void writeImpl()
	{
		if ((_boat == null) || (_activeChar == null))
		{
			return;
		}
		
		writeByte(0x5d);
		writeInt(_activeChar.getObjectId());
		writeInt(_boat.getObjectId());
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
	}
}
