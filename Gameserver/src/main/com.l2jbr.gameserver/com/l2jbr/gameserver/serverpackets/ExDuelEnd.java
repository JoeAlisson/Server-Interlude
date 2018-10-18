package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch d
 * @author KenM
 */
public class ExDuelEnd extends L2GameServerPacket {
	private final int _unk1;
	
	public ExDuelEnd(int unk1)
	{
		_unk1 = unk1;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x4e);
		
		writeInt(_unk1);
	}

}
