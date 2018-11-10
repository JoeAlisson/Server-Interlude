package org.l2j.gameserver.network.serverpackets;

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