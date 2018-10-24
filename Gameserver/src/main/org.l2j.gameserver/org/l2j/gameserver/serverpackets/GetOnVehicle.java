package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Maktakien
 */
public class GetOnVehicle extends L2GameServerPacket
{
	private final int _x;
	private final int _y;
	private final int _z;
	private final L2PcInstance _activeChar;
	private final L2BoatInstance _boat;

	public GetOnVehicle(L2PcInstance activeChar, L2BoatInstance boat, int x, int y, int z)
	{
		_activeChar = activeChar;
		_boat = boat;
		_x = x;
		_y = y;
		_z = z;
		
		_activeChar.setInBoat(true);
		_activeChar.setBoat(_boat);
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0x5c);
		writeInt(_activeChar.getObjectId());
		writeInt(_boat.getObjectId());
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		
	}
}
