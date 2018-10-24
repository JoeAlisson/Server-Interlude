package org.l2j.gameserver.serverpackets;

public class ExConfirmVariationItem extends L2GameServerPacket
{
	private final int _itemObjId;
	private final int _unk1;
	private final int _unk2;
	
	public ExConfirmVariationItem(int itemObjId)
	{
		_itemObjId = itemObjId;
		_unk1 = 1;
		_unk2 = 1;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x52);
		writeInt(_itemObjId);
		writeInt(_unk1);
		writeInt(_unk2);
	}
}
