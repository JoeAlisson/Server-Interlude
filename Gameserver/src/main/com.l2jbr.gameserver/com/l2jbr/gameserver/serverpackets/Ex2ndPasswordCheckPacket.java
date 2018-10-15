package com.l2jbr.gameserver.serverpackets;

public class Ex2ndPasswordCheckPacket extends L2GameServerPacket
{
    public static final int PASSWORD_NEW = 0;
    public static final int PASSWORD_PROMPT = 1;
    public static final int PASSWORD_OK = 2;
    private int _windowType;

    public Ex2ndPasswordCheckPacket(final int windowType) {
        this._windowType = windowType;
    }

    @Override
    protected void writeImpl() {
        writeByte(0xFE);
        writeShort(0x105);
        writeInt(this._windowType);
        writeInt(0);
    }

    @Override
    public String getType() {
        return "[S] Ex2ndPasswordCheckPacket";
    }

    @Override
    protected int packetSize() {
        return 13;
    }
}
