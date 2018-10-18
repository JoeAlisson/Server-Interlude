package com.l2jbr.gameserver.serverpackets;

/**
 * @author chris_00 close the CommandChannel Information window
 */
public class ExCloseMPCC extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x26);
	}
}
