package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.factory.ItemHelper;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.instancemanager.MercTicketManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.knownlist.NullKnownList;
import org.l2j.gameserver.model.entity.database.Augmentation;
import org.l2j.gameserver.model.entity.database.Items;
import org.l2j.gameserver.model.entity.database.repository.AugmentationsRepository;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.*;
import org.l2j.gameserver.skills.funcs.Func;
import org.l2j.gameserver.templates.xml.jaxb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElse;

public final class L2ItemInstance extends L2Object {

    private static final Logger logger = LoggerFactory.getLogger(L2ItemInstance.class);
    private static final Logger loggerItems = LoggerFactory.getLogger("item");
    private static final int MANA_CONSUMPTION_RATE = 60000;

    public static final int CHARGED_NONE = 0;
    public static final int CHARGED_SOULSHOT = 1;
    public static final int CHARGED_SPIRITSHOT = 1;
    public static final int CHARGED_BLESSED_SOULSHOT = 2; // It's a realy exists? ;-)
    public static final int CHARGED_BLESSED_SPIRITSHOT = 2;

    public static final int UNCHANGED = 0;
    public static final int ADDED = 1;
    public static final int REMOVED = 3;
    public static final int MODIFIED = 2;

    private int ownerId;
    private long count;
    // TODO move to template Or something else
    private int initCount;
    // TODO move to template Or something else
    private int time;
    private boolean decrease = false;
    private final ItemTemplate template;
    private ItemLocation location;
    private int locData;
    private int enchantLevel;
    // TODO move to template Or something else
    private int priceSell;
    // TODO move to template Or something else
    private int priceBuy;
    private boolean wear;
    private L2Augmentation augmentation;

    private long mana;
    private boolean consumingMana;

    private int type1;
    private int type2;

    private long dropTime;

    private int chargedSoulshot = CHARGED_NONE;
    private int chargedSpiritshot = CHARGED_NONE;
    private boolean chargedFishtshot;

    private boolean _protected;

    private int lastChange = 2; // 1 ??, 2 modified, 3 removed
    private boolean existsInDb; // if a record exists in DB.
    private boolean storedInDb; // if DB data is up-to-date.

    private ScheduledFuture<?> itemLootSchedule;

	public L2ItemInstance(int objectId, int itemId)  {
	    this(objectId,  ItemTable.getInstance().getTemplate(itemId));
	}

    public L2ItemInstance(int objectId, ItemTemplate item) {
        super(objectId);
        if(isNull(item)) {
            ItemHelper.releaseId(objectId);
            throw new IllegalArgumentException("Error creating item Template not found: ObjectId:" + objectId);
        }
        setKnownList(new NullKnownList(this));

        template = item;
        count = 1;
        location = ItemLocation.VOID;
        mana = template.getTime();
    }

    public final void pickupMe(L2Character player) {
        L2WorldRegion oldRegion = getPosition().getWorldRegion();
        player.broadcastPacket(new GetItem(this, player.getObjectId()));

        synchronized (this) {
            setVisible(false);
            getPosition().setWorldRegion(null);
        }

        if (MercTicketManager.getInstance().getTicketCastleId(template.getId()) > 0) {
            MercTicketManager.getInstance().removeTicket(this);
            ItemsOnGroundManager.getInstance().removeObject(this);
        }

        L2World.getInstance().removeVisibleObject(this, oldRegion);
    }

    public void setLocation(ItemLocation loc, int locData) {
        if ((loc == this.location) && (locData == this.locData)) {
            return;
        }
        this.location = loc;
        this.locData = locData;
        storedInDb = false;
    }

	public void setOwnerId(String process, int owner_id) {
		setOwnerId(owner_id);
		
		if (Config.LOG_ITEMS) {
			loggerItems.info("CHANGE: {}", process);
		}
	}

	public void setOwnerId(int ownerId) {
		if (ownerId == this.ownerId) {
			return;
		}
		
		this.ownerId = ownerId;
		storedInDb = false;
	}

	public void changeCount(String process, long count) {
	    if(!changeCount(count)) {
	        return;
        }
		loggerItems.info("CHANGE: {}", process);
	}

	public boolean changeCount(long count) {
		if (count == 0) {
			return false;
		}
		if ((count > 0) && (this.count > (Long.MAX_VALUE - count))) {
			this.count = Long.MAX_VALUE;
		} else  {
			this.count += count;
		}

		if (this.count < 0) {
			this.count = 0;
		}
		storedInDb = false;
		return true;
	}

	public void setCount(long count) {
		if (this.count == count) {
			return;
		}
		this.count = count >= -1 ? count : 0;
		storedInDb = false;
	}

    public long getReuseDelay() {
        return template.getReuseDelay();
    }

    public int getRandomDamage() {
        return isWeapon() ? ((Weapon)template).getDamage().getRandom() : 0;
    }

	public int getMpConsume() {
	    return isWeapon() ? ((Weapon)template).getConsume().getMp() : 0;
    }

	public final int getShots() {
	    return isWeapon() ? ((Weapon) template).getShots() : 0;
    }

	public final boolean  isQuestItem() {
	    return template.isQuestItem();
    }

	public final long getPrice() {
	    return template.getPrice();
    }

	public final BodyPart getBodyPart() {
	    return requireNonNullElse(template.getBodyPart(), BodyPart.NONE);
    }

	public final boolean isJewel() {
	    return isArmor() && (template.getCommissionType() == CommissionType.RING  || template.getCommissionType() == CommissionType.EARRING
                || template.getCommissionType() == CommissionType.NECKLACE  || template.getCommissionType() == CommissionType.BRACELET);
    }

	public final boolean isArmor() {
	    return template instanceof Armor;
    }

	public final boolean isWeapon() {
	    return template instanceof Weapon;
    }

	public CommissionType getCommissionType() {
	    return template.getCommissionType();
    }

	public int getWeight() {
	    return template.getWeight();
    }

	public boolean isSellable() {
	    return template.getRestriction().isSellable();
    }

    public boolean isEquipped() {
        return (location == ItemLocation.PAPERDOLL) || (location == ItemLocation.PET_EQUIP);
    }

    public boolean isEquipable() {
        return template instanceof Item && nonNull(template.getBodyPart());
    }

    public int getSlotId() {
        return ItemHelper.getItemSlot(template);
    }

    public int getPaperDoll() {
        return ItemHelper.getItemPaperDoll(template);
    }

    public int getEquipSlot() {
        return locData;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setLocation(ItemLocation loc) {
        setLocation(loc, 0);
    }

    public ItemLocation getLocation() {
        return location;
    }

    public long getCount() {
        return count;
    }
	
	public int getCustomType1()
	{
		return type1;
	}
	
	public int getCustomType2()
	{
		return type2;
	}
	
	public void setCustomType1(int newtype)
	{
		type1 = newtype;
	}
	
	public void setCustomType2(int newtype)
	{
		type2 = newtype;
	}
	
	public void setDropTime(long time)
	{
		dropTime = time;
	}
	
	public long getDropTime()
	{
		return dropTime;
	}
	
	public boolean isWear()
	{
		return wear;
	}
	
	public void setWear(boolean newwear)
	{
		wear = newwear;
	}

	public ItemType getType() {
		return template.getType();
	}

	public int getId() {
		return template.getId();
	}
	

	public final CrystalType getCrystal() {
	    return template.getCrystalInfo().getType();
    }

	public final int getCrystalCount() {
		return template.getCrystalInfo().getCount();
	}

	public long getReferencePrice() {
		return template.getPrice();
	}

	public String getName() {
		return template.getName();
	}

	public int getPriceToSell() {
		return (isConsumable() ? (int) (priceSell * Config.RATE_CONSUMABLE_COST) : priceSell);
	}

	public int getPriceToBuy()
	{
		return (isConsumable() ? (int) (priceBuy * Config.RATE_CONSUMABLE_COST) : priceBuy);
	}

	public int getLastChange()
	{
		return lastChange;
	}

	public void setLastChange(int lastChange)
	{
		this.lastChange = lastChange;
	}

	public boolean isStackable() {
		return template instanceof Item && ((Item) template).isStackable();
	}

	public boolean isDropable() {
		return !isAugmented() && template.getRestriction().isDropable();
	}

	public boolean isDestroyable()
	{
		return template.getRestriction().isDestroyable();
	}

	public boolean isTradeable() {
		return !isAugmented() && template.getRestriction().isTradeable();
	}

	private boolean isConsumable() {
	    switch (template.getCommissionType()) {
            case SOULSHOT:
            case POTION:
            case SPIRITSHOT:
                return true;
            default:
                return false;
        }
	}

	public boolean isAvailable(L2PcInstance player, boolean allowAdena) {
		return ((!isEquipped()) // Not equipped
			&& (!template.isQuestItem()) // Not Quest Item
			&& ((player.getPet() == null) || (getObjectId() != player.getPet().getControlItemId())) // Not Control item of currently summoned pet
			&& (player.getActiveEnchantItem() != this) // Not momentarily used enchant scroll
			&& (allowAdena || (getId() != 57)) && ((player.getCurrentSkill() == null) || (player.getCurrentSkill().getSkill().getItemConsumeId() != getId())) && (isTradeable()));
	}

	@Override
	public void onAction(L2PcInstance player) {
		// this causes the validate position handler to do the pickup if the location is reached.
		// mercenary tickets can only be picked up by the castle owner.
		if (((template.getId() >= 3960) && (template.getId() <= 4021) && player.isInParty()) || ((template.getId() >= 3960) && (template.getId() <= 3969) && !player.isCastleLord(1)) || ((template.getId() >= 3973) && (template.getId() <= 3982) && !player.isCastleLord(2)) || ((template.getId() >= 3986) && (template.getId() <= 3995) && !player.isCastleLord(3)) || ((template.getId() >= 3999) && (template.getId() <= 4008) && !player.isCastleLord(4)) || ((template.getId() >= 4012) && (template.getId() <= 4021) && !player.isCastleLord(5)) || ((template.getId() >= 5205) && (template.getId() <= 5214) && !player.isCastleLord(6)) || ((template.getId() >= 6779) && (template.getId() <= 6788) && !player.isCastleLord(7)) || ((template.getId() >= 7973) && (template.getId() <= 7982) && !player.isCastleLord(8)) || ((template.getId() >= 7918) && (template.getId() <= 7927) && !player.isCastleLord(9)))
		{
			if (player.isInParty())
			{
				player.sendMessage("You cannot pickup mercenaries while in a party.");
			}
			else
			{
				player.sendMessage("Only the castle lord can pickup mercenaries.");
			}
			
			player.setTarget(this);
			player.getAI().setIntention(Intention.AI_INTENTION_IDLE);
			// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
			player.sendPacket(new ActionFailed());
		}
		else
		{
			player.getAI().setIntention(Intention.AI_INTENTION_PICK_UP, this);
		}
	}
	
	/**
	 * Returns the level of enchantment of the item
	 * @return int
	 */
	public int getEnchantLevel()
	{
		return enchantLevel;
	}
	
	/**
	 * Sets the level of enchantment of the item
	 * @param enchantLevel
	 */
	public void setEnchantLevel(int enchantLevel)
	{
		if (this.enchantLevel == enchantLevel)
		{
			return;
		}
		this.enchantLevel = enchantLevel;
		storedInDb = false;
	}
	
	/**
	 * Returns whether this item is augmented or not
	 * @return true if augmented
	 */
	public boolean isAugmented()
	{
		return augmentation == null ? false : true;
	}
	
	/**
	 * Returns the augmentation object for this item
	 * @return augmentation
	 */
	public L2Augmentation getAugmentation()
	{
		return augmentation;
	}
	
	/**
	 * Sets a new augmentation
	 * @param augmentation
	 * @return return true if sucessfull
	 */
	public boolean setAugmentation(L2Augmentation augmentation)
	{
		// there shall be no previous augmentation..
		if (this.augmentation != null)
		{
			return false;
		}
		this.augmentation = augmentation;
		return true;
	}
	
	/**
	 * Remove the augmentation
	 */
	public void removeAugmentation()
	{
		if (augmentation == null)
		{
			return;
		}
		augmentation.deleteAugmentationData();
		augmentation = null;
	}


    /**
	 * Used to decrease mana (mana means life time for shadow items)
	 */
	public class ScheduleConsumeManaTask implements Runnable
	{
		private final L2ItemInstance _shadowItem;
		
		public ScheduleConsumeManaTask(L2ItemInstance item)
		{
			_shadowItem = item;
		}
		
		@Override
		public void run()
		{
			try
			{
				// decrease mana
				if (_shadowItem != null)
				{
					_shadowItem.decreaseMana(true);
				}
			}
			catch (Throwable t)
			{
			}
		}
	}
	
	/**
	 * Returns true if this item is a shadow item Shadow items have a limited life-time
	 * @return
	 */
	public boolean isShadowItem()
	{
		return (mana >= 0);
	}
	
	/**
	 * Sets the mana for this shadow item <b>NOTE</b>: does not send an inventory update packet
	 * @param mana
	 */
	public void setMana(int mana)
	{
		this.mana = mana;
	}

	public long getMana()
	{
		return mana;
	}
	
	/**
	 * Decreases the mana of this shadow item, sends a inventory update schedules a new consumption task if non is running optionally one could force a new task
	 * @param resetConsumingMana
	 */
	public void decreaseMana(boolean resetConsumingMana)
	{
		if (!isShadowItem())
		{
			return;
		}
		
		if (mana > 0)
		{
			mana--;
		}
		
		if (storedInDb)
		{
			storedInDb = false;
		}
		if (resetConsumingMana)
		{
			consumingMana = false;
		}
		
		L2PcInstance player = ((L2PcInstance) L2World.getInstance().findObject(getOwnerId()));
		if (player != null)
		{
			SystemMessage sm;
			switch ((int) mana)
			{
				case 10:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_10);
					sm.addString(getName());
					player.sendPacket(sm);
					break;
				case 5:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_5);
					sm.addString(getName());
					player.sendPacket(sm);
					break;
				case 1:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_1);
					sm.addString(getName());
					player.sendPacket(sm);
					break;
			}
			
			if (mana == 0) // The life time has expired
			{
				sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_0);
				sm.addString(getName());
				player.sendPacket(sm);
				
				// unequip
				if (isEquipped())
				{
					L2ItemInstance[] unequiped = player.getInventory().unEquipItemInSlotAndRecord(getEquipSlot());
					InventoryUpdate iu = new InventoryUpdate();
					for (L2ItemInstance element : unequiped)
					{
						player.checkSSMatch(null, element);
						iu.addModifiedItem(element);
					}
					player.sendPacket(iu);
				}
				
				if (getLocation() != ItemLocation.WAREHOUSE)
				{
					// destroy
					player.getInventory().destroyItem("L2ItemInstance", this, player, null);
					
					// send update
					InventoryUpdate iu = new InventoryUpdate();
					iu.addRemovedItem(this);
					player.sendPacket(iu);
					
					StatusUpdate su = new StatusUpdate(player.getObjectId());
					su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
					player.sendPacket(su);
					
				}
				else
				{
					player.getWarehouse().destroyItem("L2ItemInstance", this, player, null);
				}
				
				// delete from world
				L2World.getInstance().removeObject(this);
			}
			else
			{
				// Reschedule if still equipped
				if (!consumingMana && isEquipped())
				{
					scheduleConsumeManaTask();
				}
				if (getLocation() != ItemLocation.WAREHOUSE)
				{
					InventoryUpdate iu = new InventoryUpdate();
					iu.addModifiedItem(this);
					player.sendPacket(iu);
				}
			}
		}
	}
	
	private void scheduleConsumeManaTask()
	{
		consumingMana = true;
		ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleConsumeManaTask(this), MANA_CONSUMPTION_RATE);
	}
	
	/**
	 * Returns false cause item can't be attacked
	 * @return boolean false
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	/**
	 * Returns the type of charge with SoulShot of the item.
	 * @return int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public int getChargedSoulshot()
	{
		return chargedSoulshot;
	}
	
	/**
	 * Returns the type of charge with SpiritShot of the item
	 * @return int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public int getChargedSpiritshot()
	{
		return chargedSpiritshot;
	}
	
	public boolean getChargedFishshot()
	{
		return chargedFishtshot;
	}
	
	/**
	 * Sets the type of charge with SoulShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public void setChargedSoulshot(int type)
	{
		chargedSoulshot = type;
	}
	
	/**
	 * Sets the type of charge with SpiritShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public void setChargedSpiritshot(int type)
	{
		chargedSpiritshot = type;
	}
	
	public void setChargedFishshot(boolean type)
	{
		chargedFishtshot = type;
	}
	
	/**
	 * This function basically returns a set of functions from ItemTemplate/Armor/Weapon, but may add additional functions, if this particular item instance is enhanched for a particular reader.
	 * @param player : L2Character designating the reader
	 * @return Func[]
     * TODO implement
	 */
	public List<Func> getStatFuncs(L2Character player)
	{
		return  new ArrayList<>(); /*getItem().getStatFuncs(this, player);*/
	}
	
	/**
	 * Updates database.<BR>
	 * <BR>
	 * <U><I>Concept : </I></U><BR>
	 * <B>IF</B> the item exists in database :
	 * <UL>
	 * <LI><B>IF</B> the item has no owner, or has no location, or has a null quantity : remove item from database</LI>
	 * <LI><B>ELSE</B> : update item in database</LI>
	 * </UL>
	 * <B> Otherwise</B> :
	 * <UL>
	 * <LI><B>IF</B> the item hasn't a null quantity, and has a correct location, and has a correct owner : insert item in database</LI>
	 * </UL>
	 */
	public void updateDatabase()
	{
		if (isWear()) // avoid saving weared items
		{
			return;
		}
		if (existsInDb)
		{
			if ((ownerId == 0) || (location == ItemLocation.VOID) || ((count == 0) && (location != ItemLocation.LEASE)))
			{
				removeFromDb();
			}
			else
			{
				updateInDb();
			}
		}
		else
		{
			if ((count == 0) && (location != ItemLocation.LEASE))
			{
				return;
			}
			if ((location == ItemLocation.VOID) || (ownerId == 0))
			{
				return;
			}
			insertIntoDb();
		}
	}

	public static L2ItemInstance restoreFromDb(Items items) {
        int owner_id = items.getOwnerId();
        int item_id = items.getItemId();
        int objectId = items.getId();
        long count = items.getCount();
        ItemLocation loc = ItemLocation.valueOf(items.getLoc());
        int loc_data = items.getLocData();
        int enchant_level = items.getEnchantLevel();
        int custom_type1 = items.getCustomType1();
        int custom_type2 = items.getCustomType2();
        int price_sell = items.getPriceSell();
        int price_buy = items.getPriceBuy();
        int manaLeft = items.getManaLeft();

        ItemTemplate item = ItemTable.getInstance().getTemplate(item_id);
        if (item == null) {
            logger.error("Item item_id={} not known, object_id={}", item_id, objectId);
            return null;
        }

        L2ItemInstance inst = new L2ItemInstance(objectId, item);
        inst.existsInDb = true;
        inst.storedInDb = true;
        inst.ownerId = owner_id;
        inst.count = count;
        inst.enchantLevel = enchant_level;
        inst.type1 = custom_type1;
        inst.type2 = custom_type2;
        inst.location = loc;
        inst.locData = loc_data;
        inst.priceSell = price_sell;
        inst.priceBuy = price_buy;

        // Setup life time for shadow weapons
        inst.mana = manaLeft;

        // consume 1 mana
        if ((inst.mana > 0) && (inst.getLocation() == ItemLocation.PAPERDOLL))
        {
            inst.decreaseMana(false);
        }

        // if mana left is 0 delete this item
        if (inst.mana == 0) {
            inst.removeFromDb();
            return null;
        }
        else if ((inst.mana > 0) && (inst.getLocation() == ItemLocation.PAPERDOLL)) {
            inst.scheduleConsumeManaTask();
        }

        AugmentationsRepository repository = DatabaseAccess.getRepository(AugmentationsRepository.class);
        Optional<Augmentation> optionalAugmentation =  repository.findById(objectId);
        if(optionalAugmentation.isPresent()) {
            Augmentation augmentation = optionalAugmentation.get();
            inst.augmentation = new L2Augmentation(inst, augmentation.getAttributes(), augmentation.getSkill(), augmentation.getLevel(), false );
        }

        return inst;
	}
	
	/**
	 * Init a dropped L2ItemInstance and add it in the world as a visible object.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion</li> <li>Add the L2ItemInstance dropped to _visibleObjects of its L2WorldRegion</li> <li>Add the L2ItemInstance dropped in the world as a <B>visible</B> object</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects of L2World </B></FONT><BR>
	 * <BR>
	 * <B><U> Assert </U> :</B><BR>
	 * <BR>
	 * <li>_worldRegion == null <I>(L2Object is invisible at the beginning)</I></li><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Drop item</li> <li>Call Pet</li><BR>
	 * @param dropper
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void dropMe(L2Character dropper, int x, int y, int z) {
		
		synchronized (this)
		{
			// Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion
			setVisible(true);
			setPosition(x,y,z);
			
			// Add the L2ItemInstance dropped to _visibleObjects of its L2WorldRegion
			getPosition().getWorldRegion().addVisibleObject(this);
		}
		setDropTime(System.currentTimeMillis());
		
		// this can synchronize on others instancies, so it's out of
		// synchronized, to avoid deadlocks
		// Add the L2ItemInstance dropped in the world as a visible object
		L2World.getInstance().addVisibleObject(this, getPosition().getWorldRegion(), dropper);
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance().save(this);
		}
	}

    @Override
    public void decayMe() {
        super.decayMe();
        if (Config.SAVE_DROPPED_ITEM) {
            ItemsOnGroundManager.getInstance().removeObject(this);
        }
    }

    /**
	 * Update the database with values of the item
	 */
	private void updateInDb()
	{
		if (Config.ASSERT)
		{
			assert existsInDb;
		}
		if (wear)
		{
			return;
		}
		if (storedInDb)
		{
			return;
		}
		
        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.updateById(getObjectId(), ownerId, getCount(), location.name(), locData, getEnchantLevel(), priceSell,
                priceBuy, getCustomType1(), getCustomType2(), getMana());

        existsInDb = true;
        storedInDb = true;

	}
	
	/**
	 * Insert the item in database
	 */
	private void insertIntoDb()
	{
		if (wear)
		{
			return;
		}
		if (Config.ASSERT)
		{
			assert !existsInDb && (getObjectId() != 0);
		}

        Items item = new Items(getObjectId(), ownerId, template.getId(), getCount(), location.name(), locData, getEnchantLevel(),
                priceSell, priceBuy, type1, type2, mana);
        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.save(item);
        existsInDb = true;
        storedInDb = true;

	}
	
	/**
	 * Delete item from database
	 */
	private void removeFromDb()
	{
		if (wear)
		{
			return;
		}
		if (Config.ASSERT)
		{
			assert existsInDb;
		}
		
		// delete augmentation data
		if (isAugmented()) {
			augmentation.deleteAugmentationData();
		}

        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.deleteById(getObjectId());
        existsInDb = false;
        storedInDb = false;
	}
	
	/**
	 * Returns the item in String format
	 * @return String
	 */
	@Override
	public String toString()
	{
		return template.getName();
	}
	
	public void resetOwnerTimer()
	{
		if (itemLootSchedule != null)
		{
			itemLootSchedule.cancel(true);
		}
		itemLootSchedule = null;
	}
	
	public void setItemLootSchedule(ScheduledFuture<?> sf)
	{
		itemLootSchedule = sf;
	}
	
	public ScheduledFuture<?> getItemLootSchedule()
	{
		return itemLootSchedule;
	}
	
	public void setProtected(boolean is_protected)
	{
		_protected = is_protected;
	}
	
	public boolean isProtected()
	{
		return _protected;
	}
	
	public boolean isNightLure()
	{
		return (((template.getId() >= 8505) && (template.getId() <= 8513)) || (template.getId() == 8485));
	}
	
	public void setCountDecrease(boolean decrease)
	{
		this.decrease = decrease;
	}
	
	public boolean getCountDecrease()
	{
		return decrease;
	}
	
	public void setInitCount(int InitCount)
	{
		initCount = InitCount;
	}
	
	public int getInitCount()
	{
		return initCount;
	}
	
	public void restoreInitCount()
	{
		if (decrease)
		{
			count = initCount;
		}
	}
	
	public void setTime(int time)
	{
		if (time > 0)
		{
			this.time = time;
		}
		else
		{
			this.time = 0;
		}
	}
	
	public int getTime()
	{
		return time;
	}
}
