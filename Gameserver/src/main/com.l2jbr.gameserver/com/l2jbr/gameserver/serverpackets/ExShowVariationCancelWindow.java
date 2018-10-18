package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch Trigger packet
 * @author KenM
 */
public class ExShowVariationCancelWindow extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x51);
	}
}
