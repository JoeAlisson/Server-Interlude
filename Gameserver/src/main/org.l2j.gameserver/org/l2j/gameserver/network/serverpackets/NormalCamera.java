package org.l2j.gameserver.network.serverpackets;

public class NormalCamera extends L2GameServerPacket
{
	@Override
	public void writeImpl()
	{
		writeByte(0xc8);
	}
}
