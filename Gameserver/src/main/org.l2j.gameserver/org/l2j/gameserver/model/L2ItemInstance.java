package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.factory.ItemHelper;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.instancemanager.MercTicketManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.knownlist.NullKnownList;
import org.l2j.gameserver.model.entity.database.ItemEntity;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.skills.funcs.Func;
import org.l2j.gameserver.templates.xml.jaxb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.*;
import static org.l2j.commons.database.DatabaseAccess.getRepository;

public final class L2ItemInstance extends L2Object {

    private final ItemTemplate template;
    private ItemEntity entity;

    private boolean existsInDb;
    private boolean storedInDb;
    private L2PcInstance owner;

    public L2ItemInstance(int objectId, int itemId)  {
        this(objectId,  ItemTable.getInstance().getTemplate(itemId));
    }

    public L2ItemInstance(int objectId, ItemTemplate item) {
        super(objectId);
        if(isNull(item)) {
            ItemHelper.releaseId(objectId);
            throw new IllegalArgumentException("Error creating item Template not found: ObjectId:" + objectId);
        }
        template = item;
        setKnownList(new NullKnownList(this));
        entity = new ItemEntity();
        entity.setCount(1);
        entity.setLocation(ItemLocation.VOID);
        entity.setMana(template.getTime());
        entity.setSubType(ItemHelper.getSubType(template));
    }

    public L2ItemInstance(ItemTemplate template, ItemEntity entity) {
        super(entity.getObjectId());
        if(isNull(template)) {
            throw new IllegalArgumentException("Error creating item Template not found: ObjectId: " + objectId);
        }
        this.template = template;
        this.entity = entity;
        this.existsInDb = true;
        this.storedInDb = true;

    }


    // ################################################################

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

    // TODO move to template Or something else
    private int initCount;
    // TODO move to template Or something else
    private int time;
    private boolean decrease = false;
    private int locData;
    private int enchantLevel;
    // TODO move to template Or something else
    private int priceSell;
    // TODO move to template Or something else
    private int priceBuy;
    private boolean wear;
    private L2Augmentation augmentation;
    private boolean consumingMana;

    private int type1;

    private long dropTime;

    private int chargedSoulshot = CHARGED_NONE;
    private int chargedSpiritshot = CHARGED_NONE;
    private boolean chargedFishtshot;

    private boolean _protected;

    private int lastChange = 2; // 1 ??, 2 modified, 3 removed
    private ScheduledFuture<?> itemLootSchedule;

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
        if ((loc == entity.getLocation()) && (locData == entity.getLocData())) {
            return;
        }
        entity.setLocation(loc);
        entity.setLocData(locData);
        storedInDb = false;
    }

    public void setOwner(String process, L2PcInstance owner) {
		setOwner(owner);
		
		if (Config.LOG_ITEMS) {
			loggerItems.info("CHANGE Item Owner: {} reason {}", owner, process);
		}
	}

	public void setOwner(L2PcInstance owner) {
        if(owner == this.owner) {
            return;
        }
        this.owner = owner;
        entity.setOwnerId(isNull(owner) ? 0 : owner.getObjectId());
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
        entity.changeCount(count);
		storedInDb = false;
		return true;
	}

	public void setCount(long count) {
        if(entity.getCount() == count) {
            return;
        }
        entity.setCount(count >= -1 ? count : 0);
		storedInDb = false;
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
		if (((template.getId() >= 3960) && (template.getId() <= 4021) && player.isInParty()) || ((template.getId() >= 3960) && (template.getId() <= 3969) && !player.isCastleLord(1))
                || ((template.getId() >= 3973) && (template.getId() <= 3982) && !player.isCastleLord(2)) || ((template.getId() >= 3986) && (template.getId() <= 3995) && !player.isCastleLord(3))
                || ((template.getId() >= 3999) && (template.getId() <= 4008) && !player.isCastleLord(4)) || ((template.getId() >= 4012) && (template.getId() <= 4021) && !player.isCastleLord(5))
                || ((template.getId() >= 5205) && (template.getId() <= 5214) && !player.isCastleLord(6)) || ((template.getId() >= 6779) && (template.getId() <= 6788) && !player.isCastleLord(7))
                || ((template.getId() >= 7973) && (template.getId() <= 7982) && !player.isCastleLord(8)) || ((template.getId() >= 7918) && (template.getId() <= 7927) && !player.isCastleLord(9)))
		{
			if (player.isInParty()) {
				player.sendMessage("You cannot pickup mercenaries while in a party.");
			}
			else {
				player.sendMessage("Only the castle lord can pickup mercenaries.");
			}
			
			player.setTarget(this);
			player.getAI().setIntention(Intention.AI_INTENTION_IDLE);
			player.sendPacket(new ActionFailed());
		}  else {
			player.getAI().setIntention(Intention.AI_INTENTION_PICK_UP, this);
		}
	}

	public int getEnchantLevel()
	{
		return enchantLevel;
	}

	public void setEnchantLevel(int enchantLevel) {
		if (this.enchantLevel == enchantLevel) {
			return;
		}
		this.enchantLevel = enchantLevel;
		storedInDb = false;
	}

	public boolean isAugmented() {
		return nonNull(augmentation);
	}

	public L2Augmentation getAugmentation()
	{
		return augmentation;
	}

	public boolean setAugmentation(L2Augmentation augmentation) {
		if (nonNull(this.augmentation))
		{
			return false;
		}
		this.augmentation = augmentation;
		return true;
	}

	public void removeAugmentation() {
		if (isNull(augmentation)) {
			return;
		}
		augmentation.deleteAugmentationData();
		augmentation = null;
	}

    public void delete() {
        getRepository(ItemRepository.class).delete(entity);
        existsInDb = false;
        storedInDb = false;
    }

    public L2PcInstance getOwner() {
        return owner;
    }

    public class ScheduleConsumeManaTask implements Runnable {
		private final L2ItemInstance shadowItem;
		
		public ScheduleConsumeManaTask(L2ItemInstance item)
		{
			shadowItem = item;
		}
		
		@Override
		public void run() {
			try {
				if (nonNull(shadowItem)) {
				    shadowItem.decreaseMana(true);
				}
			}
			catch (Throwable t) {
			    logger.error(t.getLocalizedMessage(), t);
			}
		}
	}

	public boolean isShadowItem() {
		return (entity.getManaLeft() >= 0);
	}

	public long getMana() {
		return entity.getManaLeft();
	}

	public void decreaseMana(boolean resetConsumingMana) {
		if (!isShadowItem()) {
			return;
		}

		var currentMana = entity.getManaLeft();

		if(currentMana > 0) {
		    entity.setMana(--currentMana);
        }

        storedInDb = false;

		if (resetConsumingMana) {
			consumingMana = false;
		}

		if (nonNull(owner)) {
			SystemMessage sm = null;
			switch ((int)currentMana) {
				case 10:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_10);
					break;
				case 5:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_5);
					break;
				case 1:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_1);
					break;
                case 0:
                    sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_0);
                    break;
			}

			if(nonNull(sm)) {
                sm.addString(getName());
                owner.sendPacket(sm);
            }
			
			if (currentMana == 0) { // The life time has expired
				if (isEquipped()) {
					L2ItemInstance[] unequiped = owner.getInventory().unEquipItemInSlotAndRecord(getEquipSlot());
					InventoryUpdate iu = new InventoryUpdate();
					for (L2ItemInstance element : unequiped) {
						owner.checkSSMatch(null, element);
						iu.addModifiedItem(element);
					}
					owner.sendPacket(iu);
				} else if (getLocation() != ItemLocation.WAREHOUSE) {
					// destroy
					owner.getInventory().destroyItem("L2ItemInstance", this, owner, null);
					
					// send update
					InventoryUpdate iu = new InventoryUpdate();
					iu.addRemovedItem(this);
					owner.sendPacket(iu);
					
					StatusUpdate su = new StatusUpdate(owner.getObjectId());
					su.addAttribute(StatusUpdate.CUR_LOAD, owner.getCurrentLoad());
					owner.sendPacket(su);
					
				}  else  {
					owner.getWarehouse().destroyItem("L2ItemInstance", this, owner, null);
				}
				
				// delete from world
				L2World.getInstance().removeObject(this);
			}  else  {
				if (!consumingMana && isEquipped()) {
					scheduleConsumeManaTask();
				}

				if (getLocation() != ItemLocation.WAREHOUSE) {
					InventoryUpdate iu = new InventoryUpdate();
					iu.addModifiedItem(this);
					owner.sendPacket(iu);
				}
			}
		}
	}
	
	public void scheduleConsumeManaTask() {
		consumingMana = true;
		ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleConsumeManaTask(this), MANA_CONSUMPTION_RATE);
	}
	
	/**
	 * This function basically returns a set of functions from ItemTemplate/Armor/Weapon, but may add additional functions, if this particular item instance is enhanched for a particular player.
	 * @param player : L2Character designating the player
	 * @return Func[]
     * TODO implement
	 */
	public List<Func> getStatFuncs(L2Character player) {
		return  new ArrayList<>(); /*getItem().getStatFuncs(this, player);*/
	}

	public void updateDatabase() {
		if (isWear()){ // avoid saving weared items
		    return;
		}

		if (existsInDb) {
		    var location = entity.getLocation();
			if (isNull(owner) || (location == ItemLocation.VOID) || ((entity.getCount() == 0) && (location != ItemLocation.LEASE))) {
				removeFromDb();
			} else {
				updateInDb();
			}
		}
		else {
		    var location = entity.getLocation();
			if ((entity.getCount() == 0) && (location != ItemLocation.LEASE)) {
				return;
			}
			if ((location == ItemLocation.VOID) || (isNull(owner))) {
				return;
			}
			insertIntoDb();
		}
	}



	public final void dropMe(L2Character dropper, int x, int y, int z) {

		synchronized (this) {
			// Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion
			setVisible(true);
			setPosition(x,y,z);
		}
		setDropTime(System.currentTimeMillis());
		
		// this can synchronize on others instancies, so it's out of
		// synchronized, to avoid deadlocks
		// Add the L2ItemInstance dropped in the world as a visible object
		L2World.getInstance().addVisibleObject(this, getPosition().getWorldRegion(), dropper);
		if (Config.SAVE_DROPPED_ITEM) {
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

	private void updateInDb() {
		if (wear || storedInDb) {
			return;
		}

        getRepository(ItemRepository.class).save(entity);
        existsInDb = true;
        storedInDb = true;
	}

	private void insertIntoDb() {
		if (wear) {
			return;
		}
        getRepository(ItemRepository.class).save(entity);
        existsInDb = true;
        storedInDb = true;

	}

	private void removeFromDb() {
		if (wear) {
			return;
		}
		
		// delete augmentation data
		if (isAugmented()) {
			augmentation.deleteAugmentationData();
		}

        getRepository(ItemRepository.class).deleteById(getObjectId());
        existsInDb = false;
        storedInDb = false;
	}

	@Override
	public String toString() {
		return template.getName();
	}
	
	public void resetOwnerTimer() {
		if (itemLootSchedule != null) {
			itemLootSchedule.cancel(true);
		}
		itemLootSchedule = null;
	}
	
	public void setItemLootSchedule(ScheduledFuture<?> sf) {
		itemLootSchedule = sf;
	}
	
	public ScheduledFuture<?> getItemLootSchedule() {
		return itemLootSchedule;
	}
	
	public void setProtected(boolean is_protected) {
		_protected = is_protected;
	}
	
	public boolean isProtected() {
		return _protected;
	}
	
	public boolean isNightLure() {
		return (((template.getId() >= 8505) && (template.getId() <= 8513)) || (template.getId() == 8485));
	}
	
	public void setTime(int time) {
		if (time > 0) {
			this.time = time;
		} else {
			this.time = 0;
		}
	}
	
	public int getTime()
	{
		return time;
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
        return isArmor() && (template.getSubType() == SubType.RING  || template.getSubType() == SubType.EARRING
                || template.getSubType() == SubType.NECKLACE  || template.getSubType() == SubType.BRACELET);
    }

    public final boolean isArmor() {
        return template instanceof Armor;
    }

    public final boolean isWeapon() {
        return template instanceof Weapon;
    }

    public SubType getCommissionType() {
        return template.getSubType();
    }

    public int getWeight() {
        return template.getWeight();
    }

    public boolean isSellable() {
        return template.getRestriction().isSellable();
    }

    public boolean isEquipped() {
        return (entity.getLocation() == ItemLocation.PAPERDOLL) || (entity.getLocation() == ItemLocation.PET_EQUIP);
    }

    public boolean isEquipable() {
        return !(template instanceof Item) && nonNull(template.getBodyPart());
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

    public void setLocation(ItemLocation loc) {
        setLocation(loc, 0);
    }

    public ItemLocation getLocation() {
        return entity.getLocation();
    }

    public long getCount() {
        return entity.getCount();
    }

    public int getCustomType1()
    {
        return type1;
    }

    public int getSubType()
    {
        return entity.getSubType();
    }

    public void setCustomType1(int newtype)
    {
        type1 = newtype;
    }

    public void setCustomType2(int newtype)
    {
        entity.setSubType(newtype);
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

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return false;
    }

    public int getChargedSoulshot() {
        return chargedSoulshot;
    }

    public int getChargedSpiritshot() {
        return chargedSpiritshot;
    }

    public boolean getChargedFishshot() {
        return chargedFishtshot;
    }

    public void setChargedSoulshot(int type) {
        chargedSoulshot = type;
    }

    public void setChargedSpiritshot(int type) {
        chargedSpiritshot = type;
    }

    public void setChargedFishshot(boolean type) {
        chargedFishtshot = type;
    }

    private boolean isConsumable() {
        switch (template.getSubType()) {
            case SOULSHOT:
            case POTION:
            case SPIRITSHOT:
                return true;
            default:
                return false;
        }
    }
}
