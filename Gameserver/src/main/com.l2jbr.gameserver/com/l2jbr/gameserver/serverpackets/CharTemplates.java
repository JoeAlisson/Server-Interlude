package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.entity.database.PlayerTemplate;

import java.util.LinkedList;
import java.util.List;

public class CharTemplates extends L2GameServerPacket {
    // dddddddddddddddddddd
    private static final String _S__23_CHARTEMPLATES = "[S] 23 CharTemplates";
    private final List<PlayerTemplate> _chars = new LinkedList<>();

    public void addChar(PlayerTemplate template) {
        _chars.add(template);
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x0D);
        writeInt(_chars.size());

        for (PlayerTemplate temp : _chars) {
            writeInt(temp.getRace().ordinal());
            writeInt(temp.getPlayerClass().getId());
            writeInt(0x46); // Max Str
            writeInt(temp.getStrength());
            writeInt(0x0a); // Min Str
            writeInt(0x46); // Max Dex
            writeInt(temp.getDexterity());
            writeInt(0x0a); // Min Dex
            writeInt(0x46); // Max Con
            writeInt(temp.getConstitution());
            writeInt(0x0a); // Min CON
            writeInt(0x46); // Max Int
            writeInt(temp.getIntellienge());
            writeInt(0x0a); // Min Int
            writeInt(0x46); // Max Wit
            writeInt(temp.getWitness());
            writeInt(0x0a); // Min Wit
            writeInt(0x46); // Max Men
            writeInt(temp.getMentality());
            writeInt(0x0a); // Min Men
        }
    }
}
