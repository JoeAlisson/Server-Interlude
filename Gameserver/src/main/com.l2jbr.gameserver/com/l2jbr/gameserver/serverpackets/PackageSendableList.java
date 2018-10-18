package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2ItemInstance;

/**
 * @author -Wooden-
 */
public class PackageSendableList extends L2GameServerPacket
{
	private final L2ItemInstance[] _items;
	private final int _playerObjId;
	
	public PackageSendableList(L2ItemInstance[] items, int playerObjId)
	{
		_items = items;
		_playerObjId = playerObjId;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xC3);
		
		writeInt(_playerObjId);
		writeInt(getClient().getActiveChar().getAdena());
		writeInt(_items.length);
		for (L2ItemInstance item : _items) // format inside the for taken from SellList part use should be about the same
		{
			writeShort(item.getItem().getType1().getId());
			writeInt(item.getObjectId());
			writeInt(item.getItemId());
			writeInt(item.getCount());
			writeShort(item.getItem().getType2().getId());
			writeShort(0x00);
			writeInt(item.getItem().getBodyPart().getId());
			writeShort(item.getEnchantLevel());
			writeShort(0x00);
			writeShort(0x00);
			writeInt(item.getObjectId()); // some item identifier later used by client to answer (see RequestPackageSend) not item id nor object id maybe some freight system id??
		}
		
	}
}