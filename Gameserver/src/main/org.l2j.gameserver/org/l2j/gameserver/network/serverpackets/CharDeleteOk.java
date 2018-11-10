package org.l2j.gameserver.network.serverpackets;

public class CharDeleteOk extends L2GameServerPacket  {
	
	@Override
	protected final void writeImpl() {
		writeByte(0x1d);
	}

	@Override
	protected int packetSize() {
		return 3;
	}
}
