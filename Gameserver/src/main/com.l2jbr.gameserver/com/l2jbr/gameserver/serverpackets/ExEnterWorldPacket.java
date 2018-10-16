package com.l2jbr.gameserver.serverpackets;

public class ExEnterWorldPacket extends L2GameServerPacket {

	@Override
	protected final void writeImpl() {
		writeByte(0xFE);
		writeShort(0x1C7);
		writeInt((int) (System.currentTimeMillis() / 1000L));
	}

	@Override
	public String getType() {
		return "[S] 0xFE 0x1C7 Ex EnterWorld";
	}

	@Override
	protected int packetSize() {
		return 9;
	}
}