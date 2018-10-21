package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.templates.xml.jaxb.BaseStat;
import org.l2j.gameserver.templates.xml.jaxb.PlayerTemplate;

import java.util.Collection;

public class CharTemplates extends L2GameServerPacket {

    private static final Collection<PlayerTemplate> templates;

    static {
        templates = PlayerTemplateTable.getInstance().allTemplates();
    }

    @Override
    protected final void writeImpl() {
        PlayerTemplateTable.getInstance().allTemplates();
        writeByte(0x0D);
        writeInt(templates.size());

        for (PlayerTemplate template : templates) {
            writeInt(template.getRace().ordinal());
            writeInt(template.getClassId());

            BaseStat baseStat = template.getBaseStats();

            writeInt(0x46); // Max Str
            writeInt(baseStat.getStrength());
            writeInt(0x0a); // Min Str
            writeInt(0x46); // Max Dex
            writeInt(baseStat.getDexterity());
            writeInt(0x0a); // Min Dex
            writeInt(0x46); // Max Con
            writeInt(baseStat.getConstitution());
            writeInt(0x0a); // Min CON
            writeInt(0x46); // Max Int
            writeInt(baseStat.getIntelligence());
            writeInt(0x0a); // Min Int
            writeInt(0x46); // Max Wit
            writeInt(baseStat.getWisdom());
            writeInt(0x0a); // Min Wit
            writeInt(0x46); // Max Men
            writeInt(baseStat.getMentality());
            writeInt(0x0a); // Min Men
        }
    }
}
