package org.l2j.gameserver.network.serverpackets;

/**
 * Format: ch ddd [ddd]
 * @author KenM
 */
public class ExGetBossRecord extends L2GameServerPacket
{
	private static final String _S__FE_33_EXGETBOSSRECORD = "[S] FE:33 ExGetBossRecord";
	private final int _unk1, _unk2;
	
	public ExGetBossRecord(int val1, int val2)
	{
		_unk1 = val1;
		_unk2 = val2;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x33);
		writeInt(_unk1);
		writeInt(_unk2);
		writeInt(0x00); // list size
	}
}
