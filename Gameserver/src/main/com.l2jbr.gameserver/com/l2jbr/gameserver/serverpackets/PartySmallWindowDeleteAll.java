package com.l2jbr.gameserver.serverpackets;

public class PartySmallWindowDeleteAll extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeByte(0x50);
	}

}
