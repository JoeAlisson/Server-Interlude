package org.l2j.gameserver.serverpackets;

public class ExUserBanInfo extends L2GameServerPacket {
    private final int _points;

    public ExUserBanInfo(int points) {
        _points = points;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0xFE);
        writeShort(0x1D1);
        writeInt(_points);
    }

    @Override
    protected int packetSize() {
        return 9;
    }
}
