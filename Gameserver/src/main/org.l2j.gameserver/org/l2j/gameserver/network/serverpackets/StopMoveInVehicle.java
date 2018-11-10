package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Maktakien
 */
public class StopMoveInVehicle extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final int _boatId;

	public StopMoveInVehicle(L2PcInstance player, int boatid)
	{
		_activeChar = player;
		_boatId = boatid;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0x72);
		writeInt(_activeChar.getObjectId());
		writeInt(_boatId);
		writeInt(_activeChar.getInBoatPosition().getX());
		writeInt(_activeChar.getInBoatPosition().getY());
		writeInt(_activeChar.getInBoatPosition().getZ());
		writeInt(_activeChar.getPosition().getHeading());
	}
}
