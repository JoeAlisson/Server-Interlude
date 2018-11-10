package org.l2j.gameserver.network.serverpackets;

public class Ex2ndPasswordAckPacket extends L2GameServerPacket
{
	public static final int SUCCESS = 0x00;
	public static final int WRONG_PATTERN = 0x01;

	private int _response;

	public Ex2ndPasswordAckPacket(int response)
	{
		_response = response;
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x107);
		writeByte(0x00);
		writeInt(_response);
		writeInt(0x00);
	}
}