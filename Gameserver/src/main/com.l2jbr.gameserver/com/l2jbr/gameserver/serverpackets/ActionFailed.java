package com.l2jbr.gameserver.serverpackets;

public final class ActionFailed extends L2GameServerPacket {

	@Override
	protected void writeImpl()
	{
		writeByte(0x1F);
	}

	@Override
	protected int packetSize() {
		return 3;
	}
}
