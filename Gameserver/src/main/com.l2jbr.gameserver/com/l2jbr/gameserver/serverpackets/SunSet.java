package com.l2jbr.gameserver.serverpackets;

public class SunSet extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeByte(0x1d);
	}
}
