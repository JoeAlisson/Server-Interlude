package org.l2j.gameserver.serverpackets;

public class ChooseInventoryItem extends L2GameServerPacket {
	private final int _itemId;
	
	public ChooseInventoryItem(int itemId)
	{
		_itemId = itemId;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x6f);
		writeInt(_itemId);
	}
}
