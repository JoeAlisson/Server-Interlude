package org.l2j.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

/**
 * Format : (h) d [dS] h sub id d: number of manors [ d: id S: manor name ]
 *
 * @author l3x
 */
public class ExSendManorList extends L2GameServerPacket {

    private static  final List<String> manors = new ArrayList<>();

    static {
        manors.add("Gludio");
        manors.add("Dion");
        manors.add("Giran");
        manors.add("Oren");
        manors.add("Aden");
        manors.add("Innadril");
        manors.add("Goddard");
        manors.add("Rune");
        manors.add("Schuttgart");
    }


    @Override
    protected void writeImpl() {
        writeByte(0xFE);
        writeShort(0x22);
        writeInt(manors.size());
        for (int i = 0; i < manors.size(); i++) {
            int j = i + 1;
            writeInt(j);
            writeString(manors.get(i));
        }

    }

    @Override
    protected int packetSize() {
        return 61 + manors.size() * 4 ;
    }
}