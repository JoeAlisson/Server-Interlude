package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sample 27 00 00 01 00 // item count 04 00 // itemType1 0-weapon/ring/earring/necklace 1-armor/shield 4-item/questitem/adena c6 37 50 40 // objectId cd 09 00 00 // itemId 05 00 00 00 // count 05 00 // itemType2 0-weapon 1-shield/armor 2-ring/earring/necklace 3-questitem 4-adena 5-item 00 00 //
 * always 0 ?? 00 00 // equipped 1-yes 00 00 // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand 00 00 // always 0 ?? 00 00 // always 0 ?? format h (h dddhhhh hh) revision 377 format h (h dddhhhd hh)
 * revision 415
 *
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class ItemList extends L2GameServerPacket {
    private static Logger _log = LoggerFactory.getLogger(ItemList.class.getName());
    private final L2ItemInstance[] _items;
    private final boolean _showWindow;

    public ItemList(L2PcInstance cha, boolean showWindow) {
        _items = cha.getInventory().getItems();
        _showWindow = showWindow;
    }

    public ItemList(L2ItemInstance[] items, boolean showWindow) {
        _items = items;
        _showWindow = showWindow;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x11);
        writeShort(_showWindow ? 0x01 : 0x00);

        int count = _items.length;
        writeShort(count);

        for (L2ItemInstance temp : _items) {
            if ((temp == null) || (temp.getItem() == null)) {
                continue;
            }

            writeByte(0); // TODO implements flag
            writeInt(temp.getObjectId());
            writeInt(temp.getItemId());
            writeByte(temp.isEquipped() ? -1 : temp.getEquipSlot());
            writeLong(temp.getCount());
            writeByte(temp.getItem().getCommissionType().ordinal()); // item type2
            writeByte(temp.getCustomType1()); // item type3
            writeShort(temp.isEquipped() ? 0x01 : 0x00);
            writeLong(temp.getSlotId());
            writeByte(temp.getEnchantLevel()); // enchant level
            writeByte(temp.getCustomType2()); // item type3
            writeInt((int)temp.getMana()); // Shadow time
            writeInt((int) temp.getMana()); // temporal Life time
            writeByte(true); // non blocked

            /*if((flags & IS_AUGMENTED) == IS_AUGMENTED)
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

            writeShort(0); // TODO implements locked
            /*if(_lockItems.length > 0)
            {
                writeC(_lockType.ordinal());
                for(int i : _lockItems)
                    writeD(i);
            }*/
        }
    }
}
