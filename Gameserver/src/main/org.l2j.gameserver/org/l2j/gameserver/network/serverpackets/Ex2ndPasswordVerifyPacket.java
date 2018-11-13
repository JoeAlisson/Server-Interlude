package org.l2j.gameserver.network.serverpackets;

public class Ex2ndPasswordVerifyPacket extends L2GameServerPacket {
	public static final int PASSWORD_OK = 0x00;
	public static final int PASSWORD_WRONG = 0x01;
	public static final int PASSWORD_BAN = 0x02;

	private int wrongTentatives, mode;

	public Ex2ndPasswordVerifyPacket(int mode, int wrongTentatives) {
		this.mode = mode;
		this.wrongTentatives = wrongTentatives;
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x106);
		writeInt(mode);
		writeInt(wrongTentatives);
	}
}
