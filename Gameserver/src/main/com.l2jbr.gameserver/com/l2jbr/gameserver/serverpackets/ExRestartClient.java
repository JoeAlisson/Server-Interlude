package com.l2jbr.gameserver.serverpackets;

/**
 * Format: (ch)
 * @author -Wooden-
 */
public class ExRestartClient extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x47);
	}

}