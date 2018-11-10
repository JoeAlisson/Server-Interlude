package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.L2ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 37 // Packet Identifier <BR>
 * 01 00 // Number of ItemInfo Trame of the Packet <BR>
 * <BR>
 * 03 00 // Update type : 01-add, 02-modify, 03-remove <BR>
 * 04 00 // Item Type 1 : 00-weapon/ring/earring/necklace, 01-armor/shield, 04-item/questitem/adena <BR>
 * c6 37 50 40 // ObjectId <BR>
 * cd 09 00 00 // ItemId <BR>
 * 05 00 00 00 // Quantity <BR>
 * 05 00 // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item <BR>
 * 00 00 // Filler (always 0) <BR>
 * 00 00 // Equipped : 00-No, 01-yes <BR>
 * 00 00 // BodyPart : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand <BR>
 * 00 00 // Enchant level (pet level shown in control item) <BR>
 * 00 00 // Pet name exists or not shown in control item <BR>
 * <BR>
 * <BR>
 * format h (hh dddhhhh hh) revision 377 <BR>
 * format h (hh dddhhhd hh) revision 415 <BR>
 * <BR>
 *
 * @version $Revision: 1.3.2.2.2.4 $ $Date: 2005/03/27 15:29:39 $ Rebuild 23.2.2006 by Advi
 */

public class InventoryUpdate extends L2GameServerPacket {
    private static Logger _log = LoggerFactory.getLogger(InventoryUpdate.class.getName());

    private final List<ItemInfo> _items;

    public InventoryUpdate() {
        _items = new LinkedList<>();
        if (Config.DEBUG) {
            showDebug();
        }
    }

    public InventoryUpdate(List<ItemInfo> items) {
        _items = items;
        if (Config.DEBUG) {
            showDebug();
        }
    }

    public void addItem(L2ItemInstance item) {
        if (item != null) {
            _items.add(new ItemInfo(item));
        }
    }

    public void addNewItem(L2ItemInstance item) {
        if (item != null) {
            _items.add(new ItemInfo(item, 1));
        }
    }

    public void addModifiedItem(L2ItemInstance item) {
        if (item != null) {
            _items.add(new ItemInfo(item, 2));
        }
    }

    public void addRemovedItem(L2ItemInstance item) {
        if (item != null) {
            _items.add(new ItemInfo(item, 3));
        }
    }

    public void addItems(List<L2ItemInstance> items) {
        if (items != null) {
            for (L2ItemInstance item : items) {
                if (item != null) {
                    _items.add(new ItemInfo(item));
                }
            }
        }
    }

    private void showDebug() {
        for (ItemInfo item : _items) {
            _log.debug("oid:" + Integer.toHexString(item.getObjectId()) + " item:" + item.getItem().getName() + " last change:" + item.getChange());
        }
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x27);
        int count = _items.size();
        writeShort(count);
        for (ItemInfo item : _items) {
            writeShort(item.getChange()); // Update type : 01-add, 02-modify, 03-remove
            writeShort(item.getItem().getType().ordinal()); // Item Type 1 : 00-weapon/ring/earring/necklace, 01-armor/shield, 04-item/questitem/adena

            writeInt(item.getObjectId()); // ObjectId
            writeInt(item.getItem().getId()); // ItemId
            writeLong(item.getCount()); // Quantity
            writeShort(item.getItem().getCommissionType().ordinal()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
            writeShort(item.getCustomType1()); // Filler (always 0)
            writeShort(item.getEquipped()); // Equipped : 00-No, 01-yes
            writeInt(0); // TODO item.getItem().getBodyPart().getId()); // BodyPart : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
            writeShort(item.getEnchant()); // Enchant level (pet level shown in control item)
            writeShort(item.getCustomType2()); // Pet name exists or not shown in control item

            writeInt(item.getAugemtationBoni());
            writeInt(item.getMana());
        }
    }
}
