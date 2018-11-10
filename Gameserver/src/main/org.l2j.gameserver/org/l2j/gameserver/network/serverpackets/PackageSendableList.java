package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;

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
		writeLong(getClient().getActiveChar().getAdena());
		writeInt(_items.length);
		for (L2ItemInstance item : _items) // format inside the for taken from SellList part use should be about the same
		{
			writeShort(item.getType().ordinal());
			writeInt(item.getObjectId());
			writeInt(item.getId());
			writeLong(item.getCount());
			writeShort(item.getCommissionType().ordinal());
			writeShort(0x00);
			writeInt(0); // TODO item.getItem().getBodyPart().getId());
			writeShort(item.getEnchantLevel());
			writeShort(0x00);
			writeShort(0x00);
			writeInt(item.getObjectId()); // some item identifier later used by client to answer (see RequestPackageSend) not item id nor object id maybe some freight system id??
		}
		
	}
}