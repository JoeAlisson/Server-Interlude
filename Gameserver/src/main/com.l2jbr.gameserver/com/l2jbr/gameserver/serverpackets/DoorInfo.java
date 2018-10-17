package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2DoorInstance;

public class DoorInfo extends L2GameServerPacket {
	private static final String _S__60_DOORINFO = "[S] 4c DoorInfo";
	private final L2DoorInstance _door;
	
	public DoorInfo(L2DoorInstance door)
	{
		_door = door;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x4c);
		writeInt(_door.getObjectId());
		writeInt(_door.getDoorId());
	}
	
}
