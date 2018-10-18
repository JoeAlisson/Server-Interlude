package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch Trigger packet
 * @author KenM
 */
public class ExShowVariationMakeWindow extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x50);
	}
}
