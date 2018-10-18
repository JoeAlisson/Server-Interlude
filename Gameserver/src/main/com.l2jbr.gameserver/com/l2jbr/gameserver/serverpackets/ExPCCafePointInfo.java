package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch ddcdc
 * @author KenM
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private final int _unk1, _unk2, _unk3, _unk4;
	private int _unk5 = 0;
	
	public ExPCCafePointInfo(int val1, int val2, int val3, int val4, int val5)
	{
		_unk1 = val1;
		_unk2 = val2;
		_unk3 = val3;
		_unk4 = val4;
		_unk5 = val5;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x31);
		writeInt(_unk1);
		writeInt(_unk2);
		writeByte(_unk3);
		writeInt(_unk4);
		writeByte(_unk5);
	}
}
