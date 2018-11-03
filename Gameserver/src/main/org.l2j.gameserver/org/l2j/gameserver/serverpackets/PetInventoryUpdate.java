package org.l2j.gameserver.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.L2ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;


public class PetInventoryUpdate extends L2GameServerPacket {

    private static Logger _log = LoggerFactory.getLogger(InventoryUpdate.class.getName());
    private final List<ItemInfo> _items;


    private PetInventoryUpdate(List<ItemInfo> items) {
        _items = items;
        if (Config.DEBUG) {
            showDebug();
        }
    }

    public PetInventoryUpdate() {
        this(new LinkedList<>());
    }

    public void addItem(L2ItemInstance item) {
        _items.add(new ItemInfo(item));
    }

    public void addNewItem(L2ItemInstance item) {
        _items.add(new ItemInfo(item, 1));
    }

    public void addModifiedItem(L2ItemInstance item) {
        _items.add(new ItemInfo(item, 2));
    }

    public void addRemovedItem(L2ItemInstance item) {
        _items.add(new ItemInfo(item, 3));
    }

    public void addItems(List<L2ItemInstance> items) {
        for (L2ItemInstance item : items) {
            _items.add(new ItemInfo(item));
        }
    }

    private void showDebug() {
        for (ItemInfo item : _items) {
            _log.debug("oid:" + Integer.toHexString(item.getObjectId()) + " item:" + item.getItem().getName() + " last change:" + item.getChange());
        }
    }

    @Override
    protected final void writeImpl() {
        writeByte(0xb3);
        int count = _items.size();
        writeShort(count);
        for (ItemInfo item : _items) {
            writeShort(item.getChange());
            writeShort(item.getItem().getType().ordinal()); // item type1
            writeInt(item.getObjectId());
            writeInt(item.getItem().getId());
            writeLong(item.getCount());
            writeShort(item.getItem().getCommissionType().ordinal()); // item type2
            writeShort(0x00); // ?
            writeShort(item.getEquipped());
            // writeShort(temp.getItem().getBodyPart()); // rev 377 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
            writeInt(0); // TODO item.getItem().getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
            writeShort(item.getEnchant()); // enchant level
            writeShort(0x00); // ?
        }
    }
}
