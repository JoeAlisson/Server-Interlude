package org.l2j.gameserver.network.serverpackets;

public class ExShowSeedMapInfo extends L2GameServerPacket {

    @Override
    protected void writeImpl() {
        // TODO implement Seeds
        writeByte(0xFE);
        writeShort(0x0A2);
        writeInt(2);
        writeInt(-246857);
        writeInt(251960);
        writeInt(4331);
        writeInt(2770);
        writeInt(-213770);
        writeInt(210760);
        writeInt(4400);
        writeInt(2766);
    }
}
