package org.l2j.gameserver.serverpackets;

public class LeaveWorld extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeByte(0x84);
	}

	@Override
	protected int packetSize() {
		return 3;
	}
}
