package org.l2j.gameserver.datatables;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.factory.ItemHelper;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.L2BossInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import org.l2j.gameserver.model.entity.database.repository.PetsRepository;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.templates.xml.reader.ItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameserverMessages.getMessage;

public class ItemTable {
    private static Logger _log = LoggerFactory.getLogger(ItemTable.class);
    private static Logger _logItems = LoggerFactory.getLogger("item");

    private static ItemTable INSTANCE;

    private ItemReader itemReader;

    public static ItemTable getInstance() {
        if (isNull(INSTANCE)) {
            INSTANCE = new ItemTable();
        }
        return INSTANCE;
    }

    private ItemTable() {
        loadItems();
        _log.info(getMessage("info.items.loaded"), itemReader.size());
    }

    private void loadItems() {
        try {
            itemReader = new ItemReader();
            itemReader.readAll();
        } catch (JAXBException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public ItemTemplate getTemplate(int id) {
        return itemReader.getItemTemplate(id);
    }

    /**
     * Create the L2ItemInstance corresponding to the Item Identifier and quantitiy add logs the activity.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity</li> <li>Add the L2ItemInstance object to _allObjects of L2world</li> <li>Logs Item creation according to log settings</li><BR>
     * <BR>
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be created
     * @param count     : int Quantity of items to be created for stackable items
     * @param actor     : L2PcInstance Player requesting the item creation
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item
     */
    public L2ItemInstance createItem(String process, int itemId, long count, L2PcInstance actor, L2Object reference) {
        // Create and Init the L2ItemInstance corresponding to the Item Identifier
        L2ItemInstance item = ItemHelper.create(itemId);

        if (process.equalsIgnoreCase("loot") && !Config.AUTO_LOOT) {
            ScheduledFuture<?> itemLootShedule;
            long delay;
            // if in CommandChannel and was killing a World/RaidBoss
            if (((reference != null) && (reference instanceof L2BossInstance)) || (reference instanceof L2RaidBossInstance)) {
                if ((((L2Attackable) reference).getFirstCommandChannelAttacked() != null) && ((L2Attackable) reference).getFirstCommandChannelAttacked().meetRaidWarCondition(reference)) {
                    item.setOwner(((L2Attackable) reference).getFirstCommandChannelAttacked().getChannelLeader());
                    delay = 300000;
                } else {
                    delay = 15000;
                    item.setOwner(actor);
                }
            } else {
                item.setOwner(actor);
                delay = 15000;
            }
            itemLootShedule = ThreadPoolManager.getInstance().scheduleGeneral(new resetOwner(item), delay);
            item.setItemLootSchedule(itemLootShedule);
        }

        if (Config.DEBUG) {
            _log.debug("ItemTable: Item created  oid:" + item.getObjectId() + " itemid:" + itemId);
        }

        // Add the L2ItemInstance object to _allObjects of L2world
        L2World.getInstance().storeObject(item);

        // Set Item parameters
        if (item.isStackable() && (count > 1)) {
            item.setCount(count);
        }

        if (Config.LOG_ITEMS) {
            _logItems.info("CREATE: {}", process);
        }

        return item;
    }

    public L2ItemInstance createItem(String process, int itemId, int count, L2PcInstance actor) {
        return createItem(process, itemId, count, actor, null);
    }

    /**
     * Returns a dummy (fr = factice) item.<BR>
     * <BR>
     * <U><I>Concept :</I></U><BR>
     * Dummy item is created by setting the ID of the object in the world at null value
     *
     * @param itemId : int designating the item
     * @return L2ItemInstance designating the dummy item created
     */
    public L2ItemInstance createDummyItem(int itemId) {
        ItemTemplate item = getTemplate(itemId);
        if (item == null) {
            _log.warn("ItemTable: Item Template missing for Id: " + itemId);
            return null;
        }
        return new L2ItemInstance(0, item);
    }

    /**
     * Destroys the L2ItemInstance.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Sets L2ItemInstance parameters to be unusable</li> <li>Removes the L2ItemInstance object to _allObjects of L2world</li> <li>Logs Item delettion according to log settings</li><BR>
     * <BR>
     *
     * @param process   : String Identifier of process triggering this action
     * @param item
     * @param actor     : L2PcInstance Player requesting the item destroy
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     */
    public void destroyItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        synchronized (item) {
            item.setCount(0);
            item.setOwner(null);
            item.setLocation(ItemLocation.VOID);
            item.setLastChange(L2ItemInstance.REMOVED);

            L2World.getInstance().removeObject(item);
            ItemHelper.releaseId(item.getObjectId());

            if (Config.LOG_ITEMS) {
                _logItems.info("DELETE: {}", process);
            }

            // if it's a pet control item, delete the pet as well
            if (L2PetDataTable.isPetItem(item.getId())) {
                PetsRepository repository = DatabaseAccess.getRepository(PetsRepository.class);
                repository.deleteById(item.getObjectId());
            }
        }
    }

    public void reload() {
        //FIXME  The reader must relogin to get new item status
        synchronized (INSTANCE) {
            INSTANCE = null;
            INSTANCE = new ItemTable();
        }
    }

    protected class resetOwner implements Runnable {
        L2ItemInstance _item;

        public resetOwner(L2ItemInstance item) {
            _item = item;
        }

        @Override
        public void run() {
            _item.setOwner(null);
            _item.setItemLootSchedule(null);
        }
    }

}
