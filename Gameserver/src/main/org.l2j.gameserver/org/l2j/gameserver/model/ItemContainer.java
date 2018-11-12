package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.factory.ItemHelper;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.templates.xml.jaxb.Item;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getRepository;

/**
 * @author Advi
 */
public abstract class ItemContainer {

    protected final Map<Integer, L2ItemInstance> items;

    protected ItemContainer() {
        items = new HashMap<>();
    }

    public L2ItemInstance getItemByObjectId(int objectId) {
        return items.get(objectId);
    }

    public void restore() {
        getRepository(ItemRepository.class).findAllByOwnerAndLocation(getOwnerId(), getBaseLocation().name()).forEach(items -> {
            var item = ItemHelper.load(items);
            if (isNull(item)) {
                return;
            }

            L2World.getInstance().storeObject(item);

            // If stackable item is found in inventory just add to current quantity
            if (item.isStackable() && (getItemByItemId(item.getId()) != null)) {
                addItem("Restore", item, null, getOwner());
            } else {
                addItem(item);
            }
        });
    }

    // #######################################
    protected static final Logger logger = LoggerFactory.getLogger(ItemContainer.class);

    protected abstract L2PlayableInstance<? extends CharTemplate> getOwner();

    protected abstract ItemLocation getBaseLocation();

    /**
     * Returns the ownerID of the inventory
     *
     * @return int
     */
    public int getOwnerId() {
        return getOwner() == null ? 0 : getOwner().getObjectId();
    }

    /**
     * Returns the quantity of items in the inventory
     *
     * @return int
     */
    public int getSize() {
        return items.size();
    }

    /**
     * Returns the list of items in inventory
     *
     * @return L2ItemInstance : items in inventory
     */
    public Collection<L2ItemInstance> getItems() {
        return items.values();
    }

    /**
     * Returns the item from inventory by using its <B>itemId</B><BR>
     * <BR>
     *
     * @param itemId : int designating the ID of the item
     * @return L2ItemInstance designating the item or null if not found in inventory
     */
    public L2ItemInstance getItemByItemId(int itemId) {
        for (L2ItemInstance item : items.values()) {
            if ((item != null) && (item.getId() == itemId)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Returns the item from inventory by using its <B>itemId</B><BR>
     * <BR>
     *
     * @param itemId       : int designating the ID of the item
     * @param itemToIgnore : used during a loop, to avoid returning the same item
     * @return L2ItemInstance designating the item or null if not found in inventory
     */
    public L2ItemInstance getItemByItemId(int itemId, L2ItemInstance itemToIgnore) {
        for (L2ItemInstance item : items.values()) {
            if ((item != null) && (item.getId() == itemId) && !item.equals(itemToIgnore)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Gets count of item in the inventory
     *
     * @param itemId       : Item to look for
     * @param enchantLevel : enchant level to match on, or -1 for ANY enchant level
     * @return int corresponding to the number of items matching the above conditions.
     */
    public long getInventoryItemCount(int itemId, int enchantLevel) {
        long count = 0;

        for (L2ItemInstance item : items.values()) {
            if ((item.getId() == itemId) && ((item.getEnchantLevel() == enchantLevel) || (enchantLevel < 0))) {
                // if (item.isAvailable((L2PcInstance)getOwner(), true) || item.getItem().getSubType() == 3)//available or quest item
                if (item.isStackable()) {
                    count = item.getCount();
                } else {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Adds item to inventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be added
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance addItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        L2ItemInstance olditem = getItemByItemId(item.getId());

        // If stackable item is found in inventory just add to current quantity
        if ((olditem != null) && olditem.isStackable()) {
            long count = item.getCount();
            olditem.changeCount(process, count);
            olditem.setLastChange(L2ItemInstance.MODIFIED);

            // And destroys the item
            ItemTable.getInstance().destroyItem(process, item, actor, reference);
            item.updateDatabase();
            item = olditem;

            // Updates database
            if ((item.getId() == 57) && (count < (10000 * Config.RATE_DROP_ADENA))) {
                // Small adena changes won't be saved to database allTemplates the time
                if ((GameTimeController.getGameTicks() % 5) == 0) {
                    item.updateDatabase();
                }
            } else {
                item.updateDatabase();
            }
        }
        // If item hasn't be found in inventory, create new one
        else {
            item.setOwner(process, (L2PcInstance) getOwner()); // XXX can
            item.setLocation(getBaseLocation());
            item.setLastChange((L2ItemInstance.ADDED));

            // Add item in inventory
            addItem(item);

            // Updates database
            item.updateDatabase();
        }

        refreshWeight();
        return item;
    }

    /**
     * Adds item to inventory
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be added
     * @param count     : int Quantity of items to be added
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance addItem(String process, int itemId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = getItemByItemId(itemId);

        // If stackable item is found in inventory just add to current quantity
        if ((item != null) && item.isStackable()) {
            item.changeCount(process, count);
            item.setLastChange(L2ItemInstance.MODIFIED);
            // Updates database
            if ((itemId == 57) && (count < (10000 * Config.RATE_DROP_ADENA))) {
                // Small adena changes won't be saved to database allTemplates the time
                if ((GameTimeController.getGameTicks() % 5) == 0) {
                    item.updateDatabase();
                }
            } else {
                item.updateDatabase();
            }
        }
        // If item hasn't be found in inventory, create new one
        else {
            for (int i = 0; i < count; i++) {
                ItemTemplate template = ItemTable.getInstance().getTemplate(itemId);
                if (template == null) {
                    logger.warn( (actor != null ? "[" + actor.getName() + "] " : "") + "Invalid ItemId requested: ", itemId);
                    return null;
                }

                item = ItemTable.getInstance().createItem(process, itemId, (template instanceof Item) && ((Item)template).isStackable() ? count : 1, actor, reference);
                item.setOwner((L2PcInstance)getOwner());
                item.setLocation(getBaseLocation());
                item.setLastChange(L2ItemInstance.ADDED);

                // Add item in inventory
                addItem(item);
                // Updates database
                item.updateDatabase();

                // If stackable, end loop as entire count is included in 1 instance of item
                if ((template instanceof  Item) && ((Item)template).isStackable() || !Config.MULTIPLE_ITEM_DROP) {
                    break;
                }
            }
        }

        refreshWeight();
        return item;
    }

    /**
     * Adds Wear/Try On item to inventory<BR>
     * <BR>
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be added
     * @param actor     : L2PcInstance Player requesting the item add
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new weared item
     */
    public L2ItemInstance addWearItem(String process, int itemId, L2PcInstance actor, L2Object reference) {
        // Surch the item in the inventory of the reader
        L2ItemInstance item = getItemByItemId(itemId);

        // There is such item already in inventory
        if (item != null) {
            return item;
        }

        // Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity
        // Add the L2ItemInstance object to _allObjects of L2world
        item = ItemTable.getInstance().createItem(process, itemId, 1, actor, reference);

        // Set Item Properties
        item.setWear(true); // "Try On" Item -> Don't save it in database
        item.setOwner((L2PcInstance) getOwner());
        item.setLocation(getBaseLocation());
        item.setLastChange((L2ItemInstance.ADDED));

        // Add item in inventory and equip it if necessary (item location defined)
        addItem(item);

        // Calculate the weight loaded by reader
        refreshWeight();

        return item;
    }

    /**
     * Transfers item to another inventory
     *
     * @param process   string identifier of process triggering this action
     * @param objectId  the item object Id of the item to be transfered
     * @param count     the quantity of items to be transfered
     * @param target
     * @param actor     L2PcInstance Player requesting the item transfer
     * @param reference L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance transferItem(String process, int objectId, long count, ItemContainer target, L2PcInstance actor, L2Object reference) {
        if (target == null) {
            return null;
        }

        L2ItemInstance sourceitem = getItemByObjectId(objectId);
        if (sourceitem == null) {
            return null;
        }
        L2ItemInstance targetitem = sourceitem.isStackable() ? target.getItemByItemId(sourceitem.getId()) : null;

        synchronized (sourceitem) {
            // check if this item still present in this container
            if (getItemByObjectId(objectId) != sourceitem) {
                return null;
            }

            // Check if requested quantity is available
            if (count > sourceitem.getCount()) {
                count = sourceitem.getCount();
            }

            // If possible, move entire item object
            if ((sourceitem.getCount() == count) && (targetitem == null)) {
                removeItem(sourceitem);
                target.addItem(process, sourceitem, actor, reference);
                targetitem = sourceitem;
            } else {
                if (sourceitem.getCount() > count) // If possible, only update counts
                {
                    sourceitem.changeCount(process, -count);
                } else
                // Otherwise destroy old item
                {
                    removeItem(sourceitem);
                    ItemTable.getInstance().destroyItem(process, sourceitem, actor, reference);
                }

                if (targetitem != null) // If possible, only update counts
                {
                    targetitem.changeCount(process, count);
                } else
                // Otherwise add new item
                {
                    targetitem = target.addItem(process, sourceitem.getId(), count, actor, reference);
                }
            }

            // Updates database
            sourceitem.updateDatabase();
            if ((targetitem != sourceitem) && (targetitem != null)) {
                targetitem.updateDatabase();
            }
            if (sourceitem.isAugmented()) {
                sourceitem.getAugmentation().removeBoni(actor);
            }
            refreshWeight();
        }
        return targetitem;
    }

    /**
     * Destroy item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be destroyed
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        synchronized (item) {
            // check if item is present in this container
            if (!items.containsValue(item)) {
                return null;
            }

            removeItem(item);
            ItemTable.getInstance().destroyItem(process, item, actor, reference);

            item.updateDatabase();
            refreshWeight();
        }
        return item;
    }

    /**
     * Destroy item from inventory by using its <B>objectID</B> and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItem(String process, int objectId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }

        // Adjust item quantity
        if (item.getCount() > count) {
            synchronized (item) {
                item.changeCount(process, -count);
                item.setLastChange(L2ItemInstance.MODIFIED);

                item.updateDatabase();
                refreshWeight();
            }
            return item;
        }
        // Directly drop entire item
        return destroyItem(process, item, actor, reference);
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item identifier of the item to be destroyed
     * @param count     : int Quantity of items to be destroyed
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance destroyItemByItemId(String process, int itemId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = getItemByItemId(itemId);
        if (item == null) {
            return null;
        }

        synchronized (item) {
            // Adjust item quantity
            if (item.getCount() > count) {
                item.changeCount(process, -count);
                item.setLastChange(L2ItemInstance.MODIFIED);
            } else {
                return destroyItem(process, item, actor, reference);
            }

            item.updateDatabase();
            refreshWeight();
        }
        return item;
    }

    /**
     * Destroy allTemplates items from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public synchronized void destroyAllItems(String process, L2PcInstance actor, L2Object reference) {
        for (L2ItemInstance item : items.values()) {
            destroyItem(process, item, actor, reference);
        }
    }

    public long getAdena() {
        long count = 0;

        for (L2ItemInstance item : items.values()) {
            if (item.getId() == 57) {
                count = item.getCount();
                return count;
            }
        }

        return count;
    }

    /**
     * Adds item to inventory for further adjustments.
     *
     * @param item : L2ItemInstance to be added from inventory
     */
    protected void addItem(L2ItemInstance item) {
        items.put(item.getObjectId(), item);
    }

    /**
     * Removes item from inventory for further adjustments.
     *
     * @param item : L2ItemInstance to be removed from inventory
     */
    protected void removeItem(L2ItemInstance item) {
        items.remove(item);
    }

    /**
     * Refresh the weight of equipment loaded
     */
    protected void refreshWeight() {
    }

    /**
     * Delete item object from world
     */
    public void deleteMe() {
        try {
            updateDatabase();
        } catch (Throwable t) {
            logger.error( "deletedMe()", t);
        }
        List<L2Object> items = new LinkedList<>(this.items.values());
        this.items.clear();

        L2World.getInstance().removeObjects(items);
    }

    /**
     * Update database with items in inventory
     */
    public void updateDatabase() {
        if (getOwner() != null) {
            for (L2ItemInstance item : items.values()) {
                if (item != null) {
                    item.updateDatabase();
                }
            }
        }
    }

    public boolean validateCapacity(int slots) {
        return true;
    }

    public boolean validateWeight(int weight) {
        return true;
    }

}
