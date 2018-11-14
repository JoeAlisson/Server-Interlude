package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2StaticObjectInstance;

public class StaticObject extends L2GameServerPacket
{
	private final L2StaticObjectInstance _staticObject;

	public StaticObject(L2StaticObjectInstance StaticObject)
	{
		_staticObject = StaticObject; // staticObjectId
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x99);
		writeInt(_staticObject.getStaticObjectId()); // staticObjectId
		writeInt(_staticObject.getObjectId()); // objectId
	}
}