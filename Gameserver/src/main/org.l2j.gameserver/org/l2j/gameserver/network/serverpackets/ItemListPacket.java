package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.Collection;

public class ItemListPacket extends L2GameServerPacket {

    private final Collection<L2ItemInstance> items;
    private final boolean showWindow;

    public ItemListPacket(L2PcInstance cha, boolean showWindow) {
        items = cha.getInventory().getItems();
        this.showWindow = showWindow;
    }

    public ItemListPacket(Collection<L2ItemInstance> items, boolean showWindow) {
        this.items = items;
        this.showWindow = showWindow;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x11);
        writeShort(showWindow);

        writeShort(items.size());

        for (L2ItemInstance temp : items) {
            writeByte(0); // TODO implements flag
            writeInt(temp.getObjectId());
            writeInt(temp.getId());
            writeByte(temp.isEquipped() ? -1 : temp.getEquipSlot());
            writeLong(temp.getCount());
            writeByte(temp.getSubType()); // item type2
            writeByte(0); // TODO Race Ticket Type
            writeShort(temp.isEquipped());
            writeLong(temp.getSlotId());
            writeByte(temp.getEnchantLevel()); // enchant level
            writeByte(0); //  TODO Race Ticket Price
            writeInt((int)temp.getMana()); // Shadow time
            writeInt((int) temp.getMana()); // TODO temporal Life time
            writeByte(true); // non blocked TODO check condition

            /* TODO implement if((flags & IS_AUGMENTED) == IS_AUGMENTED)
            {
                writeD(item.getVariation1Id());
                writeD(item.getVariation2Id());
            }

            if((flags & IS_ELEMENTED) == IS_ELEMENTED)
            {
                writeH(item.getAttackElement().getId());
                writeH(attackElementValue);
                writeH(defenceFire);
                writeH(defenceWater);
                writeH(defenceWind);
                writeH(defenceEarth);
                writeH(defenceHoly);
                writeH(defenceUnholy);
            }

            if((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS)
            {
                writeD(item.getEnchantOptions()[0]);
                writeD(item.getEnchantOptions()[1]);
                writeD(item.getEnchantOptions()[2]);
            }

            if((flags & HAVE_ENSOUL) == HAVE_ENSOUL)
            {
                writeC(normalEnsouls.length);
                for(Ensoul ensoul : normalEnsouls)
                    writeD(ensoul.getId());

                writeC(specialEnsouls.length);
                for(Ensoul ensoul : specialEnsouls)
                    writeD(ensoul.getId());
            }*/
        }

        writeShort(0); // TODO implements locked
            /*if(_lockItems.length > 0)
            {
                writeC(_lockType.ordinal());
                for(int i : _lockItems)
                    writeD(i);
            }*/
    }
}
