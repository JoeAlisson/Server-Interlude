package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2DoorInstance;


public class DoorStatusUpdate extends L2GameServerPacket {
	private static final String _S__61_DOORSTATUSUPDATE = "[S] 4d DoorStatusUpdate";
	private final L2DoorInstance _door;
	
	public DoorStatusUpdate(L2DoorInstance door)
	{
		_door = door;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x4d);
		writeInt(_door.getObjectId());
		writeInt(_door.getOpen());
		writeInt(_door.getDamage());
		writeInt(_door.isEnemyOf(getClient().getActiveChar()) ? 1 : 0);
		writeInt(_door.getDoorId());
		writeInt(_door.getMaxHp());
		writeInt((int) _door.getCurrentHp());
	}
	
}
