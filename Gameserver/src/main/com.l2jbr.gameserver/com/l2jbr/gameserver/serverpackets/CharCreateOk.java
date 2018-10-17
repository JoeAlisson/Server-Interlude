package com.l2jbr.gameserver.serverpackets;

public class CharCreateOk extends L2GameServerPacket {

	@Override
	protected final void writeImpl() {
		writeByte(0x0F);
		writeInt(0x01);
	}

}
