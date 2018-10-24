package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.knownlist.NullKnownList;
import org.l2j.gameserver.model.entity.database.Augmentation;
import org.l2j.gameserver.model.entity.database.ItemTemplate;
import org.l2j.gameserver.model.entity.database.Items;
import org.l2j.gameserver.model.entity.database.repository.AugmentationsRepository;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.InventoryUpdate;
import org.l2j.gameserver.serverpackets.StatusUpdate;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.skills.funcs.Func;
import org.l2j.gameserver.templates.ItemTypeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

public final class L2ItemInstance extends L2Object {

	public L2ItemInstance(int objectId, int itemId)  {
		super(objectId);
		super.setKnownList(new NullKnownList(this));
		_itemId = itemId;
		_item = ItemTable.getInstance().getTemplate(itemId);
		if ((_itemId == 0) || (_item == null)) {
			throw new IllegalArgumentException();
		}
		_count = 1;
		_loc = ItemLocation.VOID;
		_type1 = 0;
		_type2 = 0;
		_dropTime = 0;
		_mana = _item.getDuration();
	}



	// ###############################################

	private static final Logger _log = LoggerFactory.getLogger(L2ItemInstance.class.getName());
	private static final Logger _logItems = LoggerFactory.getLogger("item");

	/** Enumeration of locations for item */
	public  enum ItemLocation
	{
		VOID,
		INVENTORY,
		PAPERDOLL,
		WAREHOUSE,
		CLANWH,
		PET,
		PET_EQUIP,
		LEASE,
		FREIGHT
	}
	
	/** ID of the owner */
	private int _ownerId;
	
	/** Quantity of the item */
	private int _count;
	/** Initial Quantity of the item */
	private int _initCount;
	/** Time after restore Item count (in Hours) */
	private int _time;
	/** Quantity of the item can decrease */
	private boolean _decrease = false;
	
	/** ID of the item */
	private final int _itemId;
	
	/** Object ItemTemplate associated to the item */
	private final ItemTemplate _item;
	
	/** Location of the item : Inventory, PaperDoll, WareHouse */
	private ItemLocation _loc;
	
	/** BodyPart where item is stored */
	private int _locData;
	
	/** Level of enchantment of the item */
	private int _enchantLevel;
	
	/** Price of the item for selling */
	private int _priceSell;
	
	/** Price of the item for buying */
	private int _priceBuy;
	
	/** Wear Item */
	private boolean _wear;
	
	/** Augmented Item */
	private L2Augmentation _augmentation = null;
	
	/** Shadow item */
	private int _mana = -1;
	private boolean _consumingMana = false;
	private static final int MANA_CONSUMPTION_RATE = 60000;
	
	/** Custom item types (used loto, race tickets) */
	private int _type1;
	private int _type2;
	
	private long _dropTime;
	
	public static final int CHARGED_NONE = 0;
	public static final int CHARGED_SOULSHOT = 1;
	public static final int CHARGED_SPIRITSHOT = 1;
	public static final int CHARGED_BLESSED_SOULSHOT = 2; // It's a realy exists? ;-)
	public static final int CHARGED_BLESSED_SPIRITSHOT = 2;
	
	/** Item charged with SoulShot (type of SoulShot) */
	private int _chargedSoulshot = CHARGED_NONE;
	/** Item charged with SpiritShot (type of SpiritShot) */
	private int _chargedSpiritshot = CHARGED_NONE;
	
	private boolean _chargedFishtshot = false;
	
	private boolean _protected;
	
	public static final int UNCHANGED = 0;
	public static final int ADDED = 1;
	public static final int REMOVED = 3;
	public static final int MODIFIED = 2;
	private int _lastChange = 2; // 1 ??, 2 modified, 3 removed
	private boolean _existsInDb; // if a record exists in DB.
	private boolean _storedInDb; // if DB data is up-to-date.
	
	private ScheduledFuture<?> itemLootShedule = null;

	
	/**
	 * Constructor of the L2ItemInstance from the objetId and the description of the item given by the ItemTemplate.
	 * @param objectId : int designating the ID of the object in the world
	 * @param item : ItemTemplate containing informations of the item
	 */
	public L2ItemInstance(int objectId, ItemTemplate item)
	{
		super(objectId);
		super.setKnownList(new NullKnownList(this));
		_itemId = item.getId();
		_item = item;
		if ((_itemId == 0) || (_item == null))
		{
			throw new IllegalArgumentException();
		}
		_count = 1;
		_loc = ItemLocation.VOID;
		_mana = _item.getDuration();
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param process : String Identifier of process triggering this action
	 * @param owner_id : int designating the ID of the owner
	 * @param creator : L2PcInstance Player requesting the item creation
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void setOwnerId(String process, int owner_id, L2PcInstance creator, L2Object reference)
	{
		setOwnerId(owner_id);
		
		if (Config.LOG_ITEMS)
		{
			_logItems.info("CHANGE: {}", process);
		}
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param owner_id : int designating the ID of the owner
	 */
	public void setOwnerId(int owner_id)
	{
		if (owner_id == _ownerId)
		{
			return;
		}
		
		_ownerId = owner_id;
		_storedInDb = false;
	}
	
	/**
	 * Returns the ownerID of the item
	 * @return int : ownerID of the item
	 */
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	/**
	 * Sets the location of the item
	 * @param loc : ItemLocation (enumeration)
	 */
	public void setLocation(ItemLocation loc)
	{
		setLocation(loc, 0);
	}
	
	/**
	 * Sets the location of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc : ItemLocation (enumeration)
	 * @param loc_data : int designating the slot where the item is stored or the village for freights
	 */
	public void setLocation(ItemLocation loc, int loc_data)
	{
		if ((loc == _loc) && (loc_data == _locData))
		{
			return;
		}
		_loc = loc;
		_locData = loc_data;
		_storedInDb = false;
	}
	
	public ItemLocation getLocation()
	{
		return _loc;
	}
	
	/**
	 * Returns the quantity of item
	 * @return int
	 */
	public int getCount()
	{
		return _count;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param process : String Identifier of process triggering this action
	 * @param count : int
	 * @param creator : L2PcInstance Player requesting the item creation
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void changeCount(String process, int count, L2PcInstance creator, L2Object reference)
	{
		if (count == 0)
		{
			return;
		}
		if ((count > 0) && (_count > (Integer.MAX_VALUE - count)))
		{
			_count = Integer.MAX_VALUE;
		}
		else
		{
			_count += count;
		}
		if (_count < 0)
		{
			_count = 0;
		}
		_storedInDb = false;

		_logItems.info("CHANGE: {}", process);

	}
	
	// No logging (function designed for shots only)
	public void changeCountWithoutTrace(String process, int count, L2PcInstance creator, L2Object reference)
	{
		if (count == 0)
		{
			return;
		}
		if ((count > 0) && (_count > (Integer.MAX_VALUE - count)))
		{
			_count = Integer.MAX_VALUE;
		}
		else
		{
			_count += count;
		}
		if (_count < 0)
		{
			_count = 0;
		}
		_storedInDb = false;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param count : int
	 */
	public void setCount(int count)
	{
		if (_count == count)
		{
			return;
		}
		
		_count = count >= -1 ? count : 0;
		_storedInDb = false;
	}
	
	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable() {
		return  _item.isEquipable();
	}
	
	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return (_loc == ItemLocation.PAPERDOLL) || (_loc == ItemLocation.PET_EQUIP);
	}
	
	/**
	 * Returns the slot where the item is stored
	 * @return int
	 */
	public int getEquipSlot()
	{
		if (Config.ASSERT)
		{
			assert (_loc == ItemLocation.PAPERDOLL) || (_loc == ItemLocation.PET_EQUIP) || (_loc == ItemLocation.FREIGHT);
		}
		return _locData;
	}
	
	/**
	 * Returns the characteristics of the item
	 * @return ItemTemplate
	 */
	public ItemTemplate getItem()
	{
		return _item;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public void setCustomType1(int newtype)
	{
		_type1 = newtype;
	}
	
	public void setCustomType2(int newtype)
	{
		_type2 = newtype;
	}
	
	public void setDropTime(long time)
	{
		_dropTime = time;
	}
	
	public long getDropTime()
	{
		return _dropTime;
	}
	
	public boolean isWear()
	{
		return _wear;
	}
	
	public void setWear(boolean newwear)
	{
		_wear = newwear;
	}

	/**
	 * Returns the type of item
	 * @return Enum
	 */
	public Enum<?> getItemType()
	{
		return _item.getType();
	}
	
	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _item.getCrystalCount(_enchantLevel);
	}
	
	/**
	 * Returns the reference price of the item
	 * @return int
	 */
	public int getReferencePrice()
	{
		return _item.getPrice();
	}
	
	/**
	 * Returns the name of the item
	 * @return String
	 */
	public String getItemName()
	{
		return _item.getName();
	}
	
	/**
	 * Returns the price of the item for selling
	 * @return int
	 */
	public int getPriceToSell()
	{
		return (isConsumable() ? (int) (_priceSell * Config.RATE_CONSUMABLE_COST) : _priceSell);
	}
	
	/**
	 * Sets the price of the item for selling <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param price : int designating the price
	 */
	public void setPriceToSell(int price)
	{
		_priceSell = price;
		_storedInDb = false;
	}
	
	/**
	 * Returns the price of the item for buying
	 * @return int
	 */
	public int getPriceToBuy()
	{
		return (isConsumable() ? (int) (_priceBuy * Config.RATE_CONSUMABLE_COST) : _priceBuy);
	}
	
	/**
	 * Sets the price of the item for buying <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param price : int
	 */
	public void setPriceToBuy(int price)
	{
		_priceBuy = price;
		_storedInDb = false;
	}
	
	/**
	 * Returns the last change of the item
	 * @return int
	 */
	public int getLastChange()
	{
		return _lastChange;
	}
	
	/**
	 * Sets the last change of the item
	 * @param lastChange : int
	 */
	public void setLastChange(int lastChange)
	{
		_lastChange = lastChange;
	}
	
	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return _item.isStackable();
	}
	
	/**
	 * Returns if item is dropable
	 * @return boolean
	 */
	public boolean isDropable()
	{
		return isAugmented() ? false : _item.isDropable();
	}
	
	/**
	 * Returns if item is destroyable
	 * @return boolean
	 */
	public boolean isDestroyable()
	{
		return _item.isDestroyable();
	}
	
	/**
	 * Returns if item is tradeable
	 * @return boolean
	 */
	public boolean isTradeable()
	{
		return isAugmented() ? false : _item.isTradeable();
	}
	
	/**
	 * Returns if item is consumable
	 * @return boolean
	 */
	public boolean isConsumable()
	{
		return _item.isConsumable();
	}
	
	/**
	 * Returns if item is available for manipulation
	 * @param player
	 * @param allowAdena
	 * @return boolean
	 */
	public boolean isAvailable(L2PcInstance player, boolean allowAdena)
	{
		return ((!isEquipped()) // Not equipped
			&& (getItem().getType2() != ItemTypeGroup.TYPE2_QUEST) // Not Quest Item
			&& ((getItem().getType2() != ItemTypeGroup.TYPE2_MONEY) || (getItem().getType1() != ItemTypeGroup.TYPE1_ARMOR_SHIELD)) // TODO: what does this mean?
			&& ((player.getPet() == null) || (getObjectId() != player.getPet().getControlItemId())) // Not Control item of currently summoned pet
			&& (player.getActiveEnchantItem() != this) // Not momentarily used enchant scroll
			&& (allowAdena || (getItemId() != 57)) && ((player.getCurrentSkill() == null) || (player.getCurrentSkill().getSkill().getItemConsumeId() != getItemId())) && (isTradeable()));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.model.L2Object#onAction(org.l2j.gameserver.model.L2PcInstance) also check constraints: only soloing castle owners may pick up mercenary tickets of their castle
	 */
	@Override
	public void onAction(L2PcInstance player)
	{
		// this causes the validate position handler to do the pickup if the location is reached.
		// mercenary tickets can only be picked up by the castle owner.
		if (((_itemId >= 3960) && (_itemId <= 4021) && player.isInParty()) || ((_itemId >= 3960) && (_itemId <= 3969) && !player.isCastleLord(1)) || ((_itemId >= 3973) && (_itemId <= 3982) && !player.isCastleLord(2)) || ((_itemId >= 3986) && (_itemId <= 3995) && !player.isCastleLord(3)) || ((_itemId >= 3999) && (_itemId <= 4008) && !player.isCastleLord(4)) || ((_itemId >= 4012) && (_itemId <= 4021) && !player.isCastleLord(5)) || ((_itemId >= 5205) && (_itemId <= 5214) && !player.isCastleLord(6)) || ((_itemId >= 6779) && (_itemId <= 6788) && !player.isCastleLord(7)) || ((_itemId >= 7973) && (_itemId <= 7982) && !player.isCastleLord(8)) || ((_itemId >= 7918) && (_itemId <= 7927) && !player.isCastleLord(9)))
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
		return _enchantLevel;
	}
	
	/**
	 * Sets the level of enchantment of the item
	 * @param enchantLevel
	 */
	public void setEnchantLevel(int enchantLevel)
	{
		if (_enchantLevel == enchantLevel)
		{
			return;
		}
		_enchantLevel = enchantLevel;
		_storedInDb = false;
	}
	
	/**
	 * Returns whether this item is augmented or not
	 * @return true if augmented
	 */
	public boolean isAugmented()
	{
		return _augmentation == null ? false : true;
	}
	
	/**
	 * Returns the augmentation object for this item
	 * @return augmentation
	 */
	public L2Augmentation getAugmentation()
	{
		return _augmentation;
	}
	
	/**
	 * Sets a new augmentation
	 * @param augmentation
	 * @return return true if sucessfull
	 */
	public boolean setAugmentation(L2Augmentation augmentation)
	{
		// there shall be no previous augmentation..
		if (_augmentation != null)
		{
			return false;
		}
		_augmentation = augmentation;
		return true;
	}
	
	/**
	 * Remove the augmentation
	 */
	public void removeAugmentation()
	{
		if (_augmentation == null)
		{
			return;
		}
		_augmentation.deleteAugmentationData();
		_augmentation = null;
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
		return (_mana >= 0);
	}
	
	/**
	 * Sets the mana for this shadow item <b>NOTE</b>: does not send an inventory update packet
	 * @param mana
	 */
	public void setMana(int mana)
	{
		_mana = mana;
	}
	
	/**
	 * Returns the remaining mana of this shadow item
	 * @return lifeTime
	 */
	public int getMana()
	{
		return _mana;
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
		
		if (_mana > 0)
		{
			_mana--;
		}
		
		if (_storedInDb)
		{
			_storedInDb = false;
		}
		if (resetConsumingMana)
		{
			_consumingMana = false;
		}
		
		L2PcInstance player = ((L2PcInstance) L2World.getInstance().findObject(getOwnerId()));
		if (player != null)
		{
			SystemMessage sm;
			switch (_mana)
			{
				case 10:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_10);
					sm.addString(getItemName());
					player.sendPacket(sm);
					break;
				case 5:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_5);
					sm.addString(getItemName());
					player.sendPacket(sm);
					break;
				case 1:
					sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_1);
					sm.addString(getItemName());
					player.sendPacket(sm);
					break;
			}
			
			if (_mana == 0) // The life time has expired
			{
				sm = new SystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_0);
				sm.addString(getItemName());
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
				if (!_consumingMana && isEquipped())
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
		_consumingMana = true;
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
		return _chargedSoulshot;
	}
	
	/**
	 * Returns the type of charge with SpiritShot of the item
	 * @return int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public int getChargedSpiritshot()
	{
		return _chargedSpiritshot;
	}
	
	public boolean getChargedFishshot()
	{
		return _chargedFishtshot;
	}
	
	/**
	 * Sets the type of charge with SoulShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SOULSHOT)
	 */
	public void setChargedSoulshot(int type)
	{
		_chargedSoulshot = type;
	}
	
	/**
	 * Sets the type of charge with SpiritShot of the item
	 * @param type : int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
	 */
	public void setChargedSpiritshot(int type)
	{
		_chargedSpiritshot = type;
	}
	
	public void setChargedFishshot(boolean type)
	{
		_chargedFishtshot = type;
	}
	
	/**
	 * This function basically returns a set of functions from ItemTemplate/Armor/Weapon, but may add additional functions, if this particular item instance is enhanched for a particular player.
	 * @param player : L2Character designating the player
	 * @return Func[]
	 */
	public List<Func> getStatFuncs(L2Character player)
	{
		return getItem().getStatFuncs(this, player);
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
		if (_existsInDb)
		{
			if ((_ownerId == 0) || (_loc == ItemLocation.VOID) || ((_count == 0) && (_loc != ItemLocation.LEASE)))
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
			if ((_count == 0) && (_loc != ItemLocation.LEASE))
			{
				return;
			}
			if ((_loc == ItemLocation.VOID) || (_ownerId == 0))
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
        int count = items.getCount();
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
            _log.error("Item item_id={} not known, object_id={}", item_id, objectId);
            return null;
        }

        L2ItemInstance inst = new L2ItemInstance(objectId, item);
        inst._existsInDb = true;
        inst._storedInDb = true;
        inst._ownerId = owner_id;
        inst._count = count;
        inst._enchantLevel = enchant_level;
        inst._type1 = custom_type1;
        inst._type2 = custom_type2;
        inst._loc = loc;
        inst._locData = loc_data;
        inst._priceSell = price_sell;
        inst._priceBuy = price_buy;

        // Setup life time for shadow weapons
        inst._mana = manaLeft;

        // consume 1 mana
        if ((inst._mana > 0) && (inst.getLocation() == ItemLocation.PAPERDOLL))
        {
            inst.decreaseMana(false);
        }

        // if mana left is 0 delete this item
        if (inst._mana == 0) {
            inst.removeFromDb();
            return null;
        }
        else if ((inst._mana > 0) && (inst.getLocation() == ItemLocation.PAPERDOLL)) {
            inst.scheduleConsumeManaTask();
        }

        AugmentationsRepository repository = DatabaseAccess.getRepository(AugmentationsRepository.class);
        Optional<Augmentation> optionalAugmentation =  repository.findById(objectId);
        if(optionalAugmentation.isPresent()) {
            Augmentation augmentation = optionalAugmentation.get();
            inst._augmentation = new L2Augmentation(inst, augmentation.getAttributes(), augmentation.getSkill(), augmentation.getLevel(), false );
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
	public final void dropMe(L2Character dropper, int x, int y, int z)
	{
		if (Config.ASSERT)
		{
			assert getPosition().getWorldRegion() == null;
		}
		
		synchronized (this)
		{
			// Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion
			setIsVisible(true);
			getPosition().setWorldPosition(x, y, z);
			getPosition().setWorldRegion(L2World.getInstance().getRegion(getPosition().getWorldPosition()));
			
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


	
	/**
	 * Update the database with values of the item
	 */
	private void updateInDb()
	{
		if (Config.ASSERT)
		{
			assert _existsInDb;
		}
		if (_wear)
		{
			return;
		}
		if (_storedInDb)
		{
			return;
		}
		
        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.updateById(getObjectId(), _ownerId, getCount(), _loc.name(), _locData, getEnchantLevel(), _priceSell,
                _priceBuy, getCustomType1(), getCustomType2(), getMana());

        _existsInDb = true;
        _storedInDb = true;

	}
	
	/**
	 * Insert the item in database
	 */
	private void insertIntoDb()
	{
		if (_wear)
		{
			return;
		}
		if (Config.ASSERT)
		{
			assert !_existsInDb && (getObjectId() != 0);
		}

        Items item = new Items(getObjectId(), _ownerId, _itemId, getCount(), _loc.name(), _locData, getEnchantLevel(),
                _priceSell, _priceBuy, _type1, _type2, _mana);
        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.save(item);
        _existsInDb = true;
        _storedInDb = true;

	}
	
	/**
	 * Delete item from database
	 */
	private void removeFromDb()
	{
		if (_wear)
		{
			return;
		}
		if (Config.ASSERT)
		{
			assert _existsInDb;
		}
		
		// delete augmentation data
		if (isAugmented()) {
			_augmentation.deleteAugmentationData();
		}

        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.deleteById(getObjectId());
        _existsInDb = false;
        _storedInDb = false;
	}
	
	/**
	 * Returns the item in String format
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "" + _item;
	}
	
	public void resetOwnerTimer()
	{
		if (itemLootShedule != null)
		{
			itemLootShedule.cancel(true);
		}
		itemLootShedule = null;
	}
	
	public void setItemLootShedule(ScheduledFuture<?> sf)
	{
		itemLootShedule = sf;
	}
	
	public ScheduledFuture<?> getItemLootShedule()
	{
		return itemLootShedule;
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
		return (((_itemId >= 8505) && (_itemId <= 8513)) || (_itemId == 8485));
	}
	
	public void setCountDecrease(boolean decrease)
	{
		_decrease = decrease;
	}
	
	public boolean getCountDecrease()
	{
		return _decrease;
	}
	
	public void setInitCount(int InitCount)
	{
		_initCount = InitCount;
	}
	
	public int getInitCount()
	{
		return _initCount;
	}
	
	public void restoreInitCount()
	{
		if (_decrease)
		{
			_count = _initCount;
		}
	}
	
	public void setTime(int time)
	{
		if (time > 0)
		{
			_time = time;
		}
		else
		{
			_time = 0;
		}
	}
	
	public int getTime()
	{
		return _time;
	}
}
