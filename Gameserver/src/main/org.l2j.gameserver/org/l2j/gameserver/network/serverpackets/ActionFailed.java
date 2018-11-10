package org.l2j.gameserver.network.serverpackets;

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
