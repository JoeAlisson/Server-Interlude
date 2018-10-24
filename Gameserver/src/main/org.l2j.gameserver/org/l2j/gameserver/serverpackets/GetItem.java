package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;


public class GetItem extends L2GameServerPacket
{
	private final L2ItemInstance _item;
	private final int _playerId;
	
	public GetItem(L2ItemInstance item, int playerId)
	{
		_item = item;
		_playerId = playerId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x0d);
		writeInt(_playerId);
		writeInt(_item.getObjectId());
		
		writeInt(_item.getX());
		writeInt(_item.getY());
		writeInt(_item.getZ());
	}
}
