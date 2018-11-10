package org.l2j.gameserver.network.serverpackets;

/**
 * Format: (ch)ddd
 */
public class ExVariationCancelResult extends L2GameServerPacket
{
	private final int _closeWindow;
	private final int _unk1;
	
	public ExVariationCancelResult(int result)
	{
		_closeWindow = 1;
		_unk1 = result;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x57);
		writeInt(_closeWindow);
		writeInt(_unk1);
	}
}