package com.l2jbr.gameserver.serverpackets;

/**
 * Format: (ch)
 * @author -Wooden-
 */
public class ShowPCCafeCouponShowUI extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x43);
	}
}