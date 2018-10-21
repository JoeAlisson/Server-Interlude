package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;

public class ValidateLocationInVehicle extends L2GameServerPacket
{
	private final L2Character _activeChar;

	public ValidateLocationInVehicle(L2Character player)
	{
		_activeChar = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x73);
		writeInt(_activeChar.getObjectId());
		writeInt(1343225858); // TODO verify vehicle object id ??
		writeInt(_activeChar.getX());
		writeInt(_activeChar.getY());
		writeInt(_activeChar.getZ());
		writeInt(_activeChar.getHeading());
	}
}
