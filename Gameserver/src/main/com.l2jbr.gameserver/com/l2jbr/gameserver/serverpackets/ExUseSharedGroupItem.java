package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch dddd
 * @author KenM
 */
public class ExUseSharedGroupItem extends L2GameServerPacket
{
	private static final String _S__FE_49_EXUSESHAREDGROUPITEM = "[S] FE:49 ExUseSharedGroupItem";
	private final int _unk1, _unk2, _unk3, _unk4;
	
	public ExUseSharedGroupItem(int unk1, int unk2, int unk3, int unk4)
	{
		_unk1 = unk1;
		_unk2 = unk2;
		_unk3 = unk3;
		_unk4 = unk4;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x49);
		
		writeInt(_unk1);
		writeInt(_unk2);
		writeInt(_unk3);
		writeInt(_unk4);
	}
}
