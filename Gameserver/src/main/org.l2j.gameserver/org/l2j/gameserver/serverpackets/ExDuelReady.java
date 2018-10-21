package org.l2j.gameserver.serverpackets;

/**
 * Format: ch d
 * @author KenM
 */
public class ExDuelReady extends L2GameServerPacket  {

	private final int _unk1;
	
	public ExDuelReady(int unk1)
	{
		_unk1 = unk1;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x4c);
		writeInt(_unk1);
	}

}
