package org.l2j.gameserver.network.serverpackets;

public class ExAutoSoulShot extends L2GameServerPacket
{
	private static final String _S__FE_12_EXAUTOSOULSHOT = "[S] FE:12 ExAutoSoulShot";
	private final int _itemId;
	private final int _type;
	
	/**
	 * 0xfe:0x12 ExAutoSoulShot (ch)dd
	 * @param itemId
	 * @param type
	 */
	public ExAutoSoulShot(int itemId, int type)
	{
		_itemId = itemId;
		_type = type;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x12); // sub id
		writeInt(_itemId);
		writeInt(_type);
	}
}
