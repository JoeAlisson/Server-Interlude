package com.l2jbr.gameserver.serverpackets;

public class NormalCamera extends L2GameServerPacket
{
	@Override
	public void writeImpl()
	{
		writeByte(0xc8);
	}
}
