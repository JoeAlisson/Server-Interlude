package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.model.TradeList.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.templates.xml.jaxb.CommissionType;

import java.util.LinkedList;
import java.util.List;

public class PcInventory extends Inventory {
    public static final int ADENA_ID = 57;
    public static final int ANCIENT_ADENA_ID = 5575;

    private final L2PcInstance _owner;
    private L2ItemInstance _adena;
    private L2ItemInstance _ancientAdena;

    public PcInventory(L2PcInstance owner) {
        _owner = owner;
    }

    @Override
    public L2PcInstance getOwner() {
        return _owner;
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PAPERDOLL;
    }

    public L2ItemInstance getAdenaInstance() {
        return _adena;
    }

    @Override
    public long getAdena() {
        return _adena != null ? _adena.getCount() : 0;
    }

    public L2ItemInstance getAncientAdenaInstance() {
        return _ancientAdena;
    }

    public long getAncientAdena() {
        return (_ancientAdena != null) ? _ancientAdena.getCount() : 0;
    }

    /**
     * Returns the list of items in inventory available for transaction
     *
     * @param allowAdena
     * @param allowAncientAdena
     * @return L2ItemInstance : items in inventory
     */
    public L2ItemInstance[] getUniqueItems(boolean allowAdena, boolean allowAncientAdena) {
        return getUniqueItems(allowAdena, allowAncientAdena, true);
    }

    public L2ItemInstance[] getUniqueItems(boolean allowAdena, boolean allowAncientAdena, boolean onlyAvailable) {
        List<L2ItemInstance> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if ((!allowAdena && (item.getId() == 57))) {
                continue;
            }
            if ((!allowAncientAdena && (item.getId() == 5575))) {
                continue;
            }

            boolean isDuplicate = false;
            for (L2ItemInstance litem : list) {
                if (litem.getId() == item.getId()) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate && (!onlyAvailable || (item.isSellable() && item.isAvailable(getOwner(), false)))) {
                list.add(item);
            }
        }

        return list.toArray(new L2ItemInstance[list.size()]);
    }

    /**
     * Returns the list of items in inventory available for transaction Allows an item to appear twice if and only if there is a difference in enchantment level.
     *
     * @param allowAdena
     * @param allowAncientAdena
     * @return L2ItemInstance : items in inventory
     */
    public L2ItemInstance[] getUniqueItemsByEnchantLevel(boolean allowAdena, boolean allowAncientAdena) {
        return getUniqueItemsByEnchantLevel(allowAdena, allowAncientAdena, true);
    }

    public L2ItemInstance[] getUniqueItemsByEnchantLevel(boolean allowAdena, boolean allowAncientAdena, boolean onlyAvailable) {
        List<L2ItemInstance> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if ((!allowAdena && (item.getId() == 57))) {
                continue;
            }
            if ((!allowAncientAdena && (item.getId() == 5575))) {
                continue;
            }

            boolean isDuplicate = false;
            for (L2ItemInstance litem : list) {
                if ((litem.getId() == item.getId()) && (litem.getEnchantLevel() == item.getEnchantLevel())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate && (!onlyAvailable || (item.isSellable() && item.isAvailable(getOwner(), false)))) {
                list.add(item);
            }
        }

        return list.toArray(new L2ItemInstance[list.size()]);
    }

    /**
     * Returns the list of allTemplates items in inventory that have a given item id.
     *
     * @param itemId
     * @return L2ItemInstance[] : matching items from inventory
     */
    public L2ItemInstance[] getAllItemsByItemId(int itemId) {
        List<L2ItemInstance> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if (item.getId() == itemId) {
                list.add(item);
            }
        }

        return list.toArray(new L2ItemInstance[list.size()]);
    }

    /**
     * Returns the list of allTemplates items in inventory that have a given item id AND a given enchantment level.
     *
     * @param itemId
     * @param enchantment
     * @return L2ItemInstance[] : matching items from inventory
     */
    public L2ItemInstance[] getAllItemsByItemId(int itemId, int enchantment) {
        List<L2ItemInstance> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if ((item.getId() == itemId) && (item.getEnchantLevel() == enchantment)) {
                list.add(item);
            }
        }

        return list.toArray(new L2ItemInstance[list.size()]);
    }

    /**
     * Returns the list of items in inventory available for transaction
     *
     * @param allowAdena
     * @return L2ItemInstance : items in inventory
     */
    public L2ItemInstance[] getAvailableItems(boolean allowAdena) {
        List<L2ItemInstance> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if ((item != null) && item.isAvailable(getOwner(), allowAdena)) {
                list.add(item);
            }
        }

        return list.toArray(new L2ItemInstance[list.size()]);
    }

    /**
     * Get allTemplates augmented items
     *
     * @return
     */
    public L2ItemInstance[] getAugmentedItems() {
        List<L2ItemInstance> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if ((item != null) && item.isAugmented()) {
                list.add(item);
            }
        }

        return list.toArray(new L2ItemInstance[list.size()]);
    }

    /**
     * Returns the list of items in inventory available for transaction adjusted by tradeList
     *
     * @param tradeList
     * @return L2ItemInstance : items in inventory
     */
    public TradeList.TradeItem[] getAvailableItems(TradeList tradeList) {
        List<TradeList.TradeItem> list = new LinkedList<>();
        for (L2ItemInstance item : _items) {
            if (item.isAvailable(getOwner(), false)) {
                TradeList.TradeItem adjItem = tradeList.adjustAvailableItem(item);
                if (adjItem != null) {
                    list.add(adjItem);
                }
            }
        }

        return list.toArray(new TradeList.TradeItem[list.size()]);
    }

    /**
     * Adjust TradeItem according his status in inventory
     *
     * @param item : L2ItemInstance to be adjusted
     */
    public void adjustAvailableItem(TradeItem item) {
        for (L2ItemInstance adjItem : _items) {
            if (adjItem.getId() == item.getItem().getId()) {
                item.setObjectId(adjItem.getObjectId());
                item.setEnchant(adjItem.getEnchantLevel());

                if (adjItem.getCount() < item.getCount()) {
                    item.setCount(adjItem.getCount());
                }

                return;
            }
        }

        item.setCount(0);
    }

    /**
     * Adds adena to PCInventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be added
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void addAdena(String process, long count, L2PcInstance actor, L2Object reference) {
        if (count > 0) {
            addItem(process, ADENA_ID, count, actor, reference);
        }
    }

    /**
     * Removes adena to PCInventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be removed
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void reduceAdena(String process, long count, L2PcInstance actor, L2Object reference) {
        if (count > 0) {
            destroyItemByItemId(process, ADENA_ID, count, actor, reference);
        }
    }

    /**
     * Adds specified amount of ancient adena to reader inventory.
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be added
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void addAncientAdena(String process, long count, L2PcInstance actor, L2Object reference) {
        if (count > 0) {
            addItem(process, ANCIENT_ADENA_ID, count, actor, reference);
        }
    }

    /**
     * Removes specified amount of ancient adena from reader inventory.
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of adena to be removed
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void reduceAncientAdena(String process, int count, L2PcInstance actor, L2Object reference) {
        if (count > 0) {
            destroyItemByItemId(process, ANCIENT_ADENA_ID, count, actor, reference);
        }
    }

    /**
     * Adds item in inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be added
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    @Override
    public L2ItemInstance addItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        item = super.addItem(process, item, actor, reference);

        if ((item != null) && (item.getId() == ADENA_ID) && !item.equals(_adena)) {
            _adena = item;
        }

        if ((item != null) && (item.getId() == ANCIENT_ADENA_ID) && !item.equals(_ancientAdena)) {
            _ancientAdena = item;
        }

        return item;
    }

    /**
     * Adds item in inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be added
     * @param count     : int Quantity of items to be added
     * @param actor     : L2PcInstance Player requesting the item creation
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    @Override
    public L2ItemInstance addItem(String process, int itemId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = super.addItem(process, itemId, count, actor, reference);

        if ((item != null) && (item.getId() == ADENA_ID) && !item.equals(_adena)) {
            _adena = item;
        }

        if ((item != null) && (item.getId() == ANCIENT_ADENA_ID) && !item.equals(_ancientAdena)) {
            _ancientAdena = item;
        }

        return item;
    }

    /**
     * Transfers item to another inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param count     : int Quantity of items to be transfered
     * @param actor     : L2PcInstance Player requesting the item transfer
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    @Override
    public L2ItemInstance transferItem(String process, int objectId, long count, ItemContainer target, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = super.transferItem(process, objectId, count, target, actor, reference);

        if ((_adena != null) && ((_adena.getCount() <= 0) || (_adena.getOwnerId() != getOwnerId()))) {
            _adena = null;
        }

        if ((_ancientAdena != null) && ((_ancientAdena.getCount() <= 0) || (_ancientAdena.getOwnerId() != getOwnerId()))) {
            _ancientAdena = null;
        }

        return item;
    }

    /**
     * Destroy item from inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be destroyed
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public L2ItemInstance destroyItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        item = super.destroyItem(process, item, actor, reference);

        if ((_adena != null) && (_adena.getCount() <= 0)) {
            _adena = null;
        }

        if ((_ancientAdena != null) && (_ancientAdena.getCount() <= 0)) {
            _ancientAdena = null;
        }

        return item;
    }

    /**
     * Destroys item from inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public L2ItemInstance destroyItem(String process, int objectId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = super.destroyItem(process, objectId, count, actor, reference);

        if ((_adena != null) && (_adena.getCount() <= 0)) {
            _adena = null;
        }

        if ((_ancientAdena != null) && (_ancientAdena.getCount() <= 0)) {
            _ancientAdena = null;
        }

        return item;
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public L2ItemInstance destroyItemByItemId(String process, int itemId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = super.destroyItemByItemId(process, itemId, count, actor, reference);

        if ((_adena != null) && (_adena.getCount() <= 0)) {
            _adena = null;
        }

        if ((_ancientAdena != null) && (_ancientAdena.getCount() <= 0)) {
            _ancientAdena = null;
        }

        return item;
    }

    /**
     * Drop item from inventory and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be dropped
     * @param actor     : L2PcInstance Player requesting the item drop
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public L2ItemInstance dropItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        item = super.dropItem(process, item, actor, reference);

        if ((_adena != null) && ((_adena.getCount() <= 0) || (_adena.getOwnerId() != getOwnerId()))) {
            _adena = null;
        }

        if ((_ancientAdena != null) && ((_ancientAdena.getCount() <= 0) || (_ancientAdena.getOwnerId() != getOwnerId()))) {
            _ancientAdena = null;
        }

        return item;
    }

    /**
     * Drop item from inventory by using its <B>objectID</B> and checks _adena and _ancientAdena
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be dropped
     * @param count     : int Quantity of items to be dropped
     * @param actor     : L2PcInstance Player requesting the item drop
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    @Override
    public L2ItemInstance dropItem(String process, int objectId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = super.dropItem(process, objectId, count, actor, reference);

        if ((_adena != null) && ((_adena.getCount() <= 0) || (_adena.getOwnerId() != getOwnerId()))) {
            _adena = null;
        }

        if ((_ancientAdena != null) && ((_ancientAdena.getCount() <= 0) || (_ancientAdena.getOwnerId() != getOwnerId()))) {
            _ancientAdena = null;
        }

        return item;
    }

    /**
     * <b>Overloaded</b>, when removes item from inventory, remove also owner shortcuts.
     *
     * @param item : L2ItemInstance to be removed from inventory
     */
    @Override
    protected void removeItem(L2ItemInstance item) {
        // Removes any reference to the item from Shortcut bar
        getOwner().removeItemFromShortCut(item.getObjectId());

        // Removes active Enchant Scroll
        if (item.equals(getOwner().getActiveEnchantItem())) {
            getOwner().setActiveEnchantItem(null);
        }

        if (item.getId() == ADENA_ID) {
            _adena = null;
        } else if (item.getId() == ANCIENT_ADENA_ID) {
            _ancientAdena = null;
        }

        super.removeItem(item);
    }

    /**
     * Refresh the weight of equipment loaded
     */
    @Override
    public void refreshWeight() {
        super.refreshWeight();
        getOwner().refreshOverloaded();
    }

    /**
     * Get back items in inventory from database
     */
    @Override
    public void restore() {
        super.restore();
        _adena = getItemByItemId(ADENA_ID);
        _ancientAdena = getItemByItemId(ANCIENT_ADENA_ID);
    }

    public static int[][] restoreVisibleInventory(int objectId) {
        int[][] paperdoll = new int[0x21][3];

        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.findAllByOwnerAndLocation(objectId, "PAPERDOLL").forEach(items -> {
            int slot = items.getLocData();
            paperdoll[slot][0] = items.getId();
            paperdoll[slot][1] = items.getItemId();
            paperdoll[slot][2] = items.getEnchantLevel();
        });
        return paperdoll;
    }

    public boolean validateCapacity(L2ItemInstance item) {
        int slots = 0;

        if (!(item.isStackable() && (getItemByItemId(item.getId()) != null)) && (item.getCommissionType() != CommissionType.HERB)) {
            slots++;
        }

        return validateCapacity(slots);
    }

    public boolean validateCapacity(List<L2ItemInstance> items) {
        int slots = 0;

        for (L2ItemInstance item : items) {
            if (!(item.isStackable() && (getItemByItemId(item.getId()) != null))) {
                slots++;
            }
        }

        return validateCapacity(slots);
    }

    public boolean validateCapacityByItemId(int ItemId) {
        int slots = 0;

        L2ItemInstance invItem = getItemByItemId(ItemId);
        if (!((invItem != null) && invItem.isStackable())) {
            slots++;
        }

        return validateCapacity(slots);
    }

    @Override
    public boolean validateCapacity(int slots) {
        return ((_items.size() + slots) <= _owner.getInventoryLimit());
    }

    @Override
    public boolean validateWeight(int weight) {
        return ((_totalWeight + weight) <= _owner.getMaxLoad());
    }
}
