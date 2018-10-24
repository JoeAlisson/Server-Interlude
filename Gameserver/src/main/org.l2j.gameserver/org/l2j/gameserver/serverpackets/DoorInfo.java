package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2DoorInstance;

public class DoorInfo extends L2GameServerPacket {
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
