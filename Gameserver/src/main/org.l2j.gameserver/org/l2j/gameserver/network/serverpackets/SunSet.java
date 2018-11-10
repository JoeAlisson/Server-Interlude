package org.l2j.gameserver.network.serverpackets;

public class SunSet extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeByte(0x1d);
	}
}
