package com.l2jbr.gameserver.serverpackets;

public class SunRise extends L2GameServerPacket {
	@Override
	protected final void writeImpl()
	{
		writeByte(0x1c);
	}

}
