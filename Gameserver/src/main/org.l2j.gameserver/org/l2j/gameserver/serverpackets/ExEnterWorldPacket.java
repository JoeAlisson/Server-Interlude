package org.l2j.gameserver.serverpackets;

public class ExEnterWorldPacket extends L2GameServerPacket {

	@Override
	protected final void writeImpl() {
		writeByte(0xFE);
		writeShort(0x1C7);
		writeInt((int) (System.currentTimeMillis() / 1000L));
	}

	@Override
	protected int packetSize() {
		return 9;
	}
}