package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Object;

public class DeleteObject extends L2GameServerPacket {
	private final int _objectId;
	
	public DeleteObject(L2Object obj)
	{
		_objectId = obj.getObjectId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x12);
		writeInt(_objectId);
		writeInt(0x00); // c2
	}
}
