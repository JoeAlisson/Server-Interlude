package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Object;

/**
 * sample 0000: 0c 9b da 12 40 ....@ format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:40 $
 */
public class Revive extends L2GameServerPacket
{
	private final int _objectId;
	
	public Revive(L2Object obj)
	{
		_objectId = obj.getObjectId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x07);
		writeInt(_objectId);
	}
}
