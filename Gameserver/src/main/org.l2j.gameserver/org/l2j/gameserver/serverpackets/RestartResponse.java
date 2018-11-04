package org.l2j.gameserver.serverpackets;

public class RestartResponse extends L2GameServerPacket {

	@Override
	protected final void writeImpl() {
		writeByte(0x71);
		writeInt(0x01); // 01-ok
		writeString("Bye");
	}
}
