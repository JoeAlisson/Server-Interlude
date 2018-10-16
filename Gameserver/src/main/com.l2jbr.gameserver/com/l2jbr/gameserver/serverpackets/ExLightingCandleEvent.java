package com.l2jbr.gameserver.serverpackets;

/**
 *
 * @author monithly
 */
public class ExLightingCandleEvent extends L2GameServerPacket
{
	public static final int ENABLED = 1;
	public static final int DISABLED = 0;

	private final int _value;

	public ExLightingCandleEvent(int value)
	{
		_value = value;
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x117);
		writeShort(_value);	// Available
	}

	@Override
	public String getType() {
		return "[S] 0xFE 0x117 EX Lighting Candle Event ";
	}
}
