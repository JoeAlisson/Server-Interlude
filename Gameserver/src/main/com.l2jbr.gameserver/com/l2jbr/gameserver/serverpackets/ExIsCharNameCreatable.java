package com.l2jbr.gameserver.serverpackets;

public class ExIsCharNameCreatable extends L2GameServerPacket {

	public static final int SUCCESS = -1;
	public static final int UNABLE_TO_CREATE_A_CHARACTER = 0x00;
	public static final int TOO_MANY_CHARACTERS = 0x01;
	public static final int NAME_ALREADY_EXISTS = 0x02;
	public static final int ENTER_CHAR_NAME__MAX_16_CHARS = 0x03;
	public static final int WRONG_NAME = 0x04;
	public static final int WRONG_SERVER = 0x05;
	public static final int DONT_CREATE_CHARS_ON_THIS_SERVER = 0x06;
	public static final int DONT_USE_ENG_CHARS = 0x07;

	public int _errorCode;

	public ExIsCharNameCreatable(int errorCode)
	{
		_errorCode = errorCode;
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x10B);
		writeInt(_errorCode);
	}

    @Override
    protected int packetSize() {
        return super.packetSize() + 7;
    }
}
