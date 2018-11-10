package org.l2j.gameserver.network.serverpackets;

public class ExConnectedTimeAndGettableReward extends L2GameServerPacket {

	@Override
	protected final void writeImpl() {
		writeByte(0xFE);
		writeShort(0x1A9);
		writeInt(0x00);       // unk 1
		writeInt(0x00);       // unk 2
		writeInt(0x00);       // unk 3
		writeInt(0x00);       // unk 4
		writeInt(0x00);       // unk 5
		writeInt(0x00);       // unk 6
		writeInt(0x00);       // unk 7
		writeInt(0x00);       // unk 8
		writeInt(0x00);       // unk 9
		writeInt(0x00);       // unk 10
		writeInt(0x00);       // unk 11
	}
}