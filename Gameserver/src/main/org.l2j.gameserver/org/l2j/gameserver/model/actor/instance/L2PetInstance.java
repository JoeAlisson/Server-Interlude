package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.Intention;
import org.l2j.gameserver.factory.IdFactory;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.stat.PetStat;
import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.l2j.gameserver.model.entity.database.Pets;
import org.l2j.gameserver.model.entity.database.PetsStats;
import org.l2j.gameserver.model.entity.database.repository.PetsRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static org.l2j.gameserver.templates.base.NpcType.L2BabyPet;

public class L2PetInstance extends L2Summon
{
	protected static final Logger _logPet = LoggerFactory.getLogger(L2PetInstance.class.getName());
	
	// private byte _pvpFlag;
	private int _curFed;
	private final PetInventory _inventory;
	private final int _controlItemId;
	private boolean _respawned;
	private final boolean _mountable;
	
	private Future<?> _feedTask;
	private int _feedTime;
	protected boolean _feedMode;
	
	private PetsStats _data;
	
	/** The Experience before the last Death Penalty */
	private long _expBeforeDeath = 0;
	private static final int FOOD_ITEM_CONSUME_COUNT = 5;
	
	public final PetsStats getPetData() {
		if (_data == null)
		{
			_data = L2PetDataTable.getInstance().getPetData(getNpcId(), getStat().getLevel());
		}
		
		return _data;
	}
	
	public final void setPetData(PetsStats petsStats)
	{
		_data = petsStats;
	}

    /**
	 * Manage Feeding Task.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Feed or kill the pet depending on hunger level</li> <li>If pet has food in inventory and feed level drops below 55% then consume food from inventory</li> <li>Send a broadcastStatusUpdate packet for this L2PetInstance</li><BR>
	 * <BR>
	 */
	
	class FeedTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				// if pet is attacking
				if (isAttackingNow())
				{
					// if its not already on battleFeed mode
					if (!_feedMode)
					{
						startFeed(true); // switching to battle feed
					}
					else
					// if its on battleFeed mode
					if (_feedMode)
					{
						startFeed(false); // normal feed
					}
				}
				
				if (getCurrentFed() > FOOD_ITEM_CONSUME_COUNT)
				{
					// eat
					setCurrentFed(getCurrentFed() - FOOD_ITEM_CONSUME_COUNT);
				}
				else
				{
					// go back to pet control item, or simply said, unsummon it
					setCurrentFed(0);
					stopFeed();
					unSummon(getOwner());
					getOwner().sendMessage("Your pet is too hungry to stay summoned.");
				}
				
				int foodId = L2PetDataTable.getFoodItemId(template.getId());
				if (foodId == 0)
				{
					return;
				}
				
				L2ItemInstance food = null;
				food = getInventory().getItemByItemId(foodId);
				
				if ((food != null) && (getCurrentFed() < (0.55 * getMaxFed())))
				{
					if (destroyItem("Feed", food.getObjectId(), 1, null, false))
					{
						setCurrentFed(getCurrentFed() + (100));
						if (getOwner() != null)
						{
							SystemMessage sm = new SystemMessage(SystemMessageId.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY);
							sm.addItemName(foodId);
							getOwner().sendPacket(sm);
						}
					}
				}
				
				broadcastStatusUpdate();
			}
			catch (Throwable e)
			{
				if (Config.DEBUG)
				{
					_logPet.warn("Pet [#" + getObjectId() + "] a feed task error has occurred: " + e);
				}
			}
		}
	}
	
	public synchronized static L2PetInstance spawnPet(NpcTemplate template, L2PcInstance owner, L2ItemInstance control)
	{
		if (L2World.getInstance().getPet(owner.getObjectId()) != null)
		{
			return null; // owner has a pet listed in world
		}
		
		L2PetInstance pet = restore(control, template, owner);
		// add the pet instance to world
		if (pet != null)
		{
			L2World.getInstance().addPet(owner.getObjectId(), pet);
		}
		
		return pet;
	}
	
	public L2PetInstance(int objectId, NpcTemplate template, L2PcInstance owner, L2ItemInstance control)
	{
		super(objectId, template, owner);
		super.setStat(new PetStat(this));
		
		_controlItemId = control.getObjectId();
		
		// Pet's initial level is supposed to be read from DB
		// Pets start at :
		// Wolf : Level 15
		// Hatcling : Level 35
		// Tested and confirmed on official servers
		// Sin-eaters are defaulted at the owner's level
		if (template.getId() == 12564)
		{
			getStat().setLevel((byte) getOwner().getLevel());
		}
		else
		{
			getStat().setLevel(template.getLevel());
		}
		
		_inventory = new PetInventory(this);
		
		int npcId = template.getId();
		_mountable = L2PetDataTable.isMountable(npcId);
	}
	
	@Override
	public PetStat getStat()
	{
		if ((super.getStat() == null) || !(super.getStat() instanceof PetStat))
		{
			setStat(new PetStat(this));
		}
		return (PetStat) super.getStat();
	}
	
	@Override
	public double getLevelMod()
	{
		return ((100.0 - 11) + getLevel()) / 100.0;
	}
	
	public boolean isRespawned()
	{
		return _respawned;
	}
	
	@Override
	public int getSummonType()
	{
		return 2;
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		boolean isOwner = player.getObjectId() == getOwner().getObjectId();
		boolean thisIsTarget = (player.getTarget() != null) && (player.getTarget().getObjectId() == getObjectId());
		
		if (isOwner && thisIsTarget)
		{
			if (isOwner && (player != getOwner()))
			{
				// update owner
				updateRefOwner(player);
			}
			player.sendPacket(new PetStatusShow(this));
			player.sendPacket(new ActionFailed());
		}
		else
		{
			if (Config.DEBUG)
			{
				_logPet.debug("new target selected:" + getObjectId());
			}
			player.setTarget(this);
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
		}
	}
	
	@Override
	public int getControlItemId()
	{
		return _controlItemId;
	}
	
	public L2ItemInstance getControlItem()
	{
		return getOwner().getInventory().getItemByObjectId(_controlItemId);
	}
	
	public int getCurrentFed()
	{
		return _curFed;
	}
	
	public void setCurrentFed(int num)
	{
		_curFed = num > getMaxFed() ? getMaxFed() : num;
	}
	
	// public void setPvpFlag(byte pvpFlag) { _pvpFlag = pvpFlag; }
	
	@Override
	public void setPkKills(int pkKills)
	{
		_pkKills = pkKills;
	}
	
	/**
	 * Returns the pet's currently equipped weapon instance (if any).
	 */
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		for (L2ItemInstance item : getInventory().getItems())
		{
			if ((item.getLocation() == ItemLocation.PET_EQUIP) && (item.isWeapon()))
			{
				return item;
			}
		}
		
		return null;
	}
	
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		// temporary? unavailable
		return null;
	}
	
	@Override
	public PetInventory getInventory()
	{
		return _inventory;
	}
	
	/**
	 * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	@Override
	public boolean destroyItem(String process, int objectId, long count, L2Object reference, boolean sendMessage)
	{
		L2ItemInstance item = _inventory.destroyItem(process, objectId, count, getOwner(), reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
			}
			
			return false;
		}
		
		// Send Pet inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		petIU.addItem(item);
		getOwner().sendPacket(petIU);
		
		if (sendMessage)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
			sm.addNumber(count);
			sm.addItemName(item.getId());
			getOwner().sendPacket(sm);
		}
		return true;
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	@Override
	public boolean destroyItemByItemId(String process, int itemId, long count, L2Object reference, boolean sendMessage)
	{
		L2ItemInstance item = _inventory.destroyItemByItemId(process, itemId, count, getOwner(), reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
			}
			return false;
		}
		
		// Send Pet inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		petIU.addItem(item);
		getOwner().sendPacket(petIU);
		
		if (sendMessage)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
			sm.addNumber(count);
			sm.addItemName(itemId);
			getOwner().sendPacket(sm);
		}
		
		return true;
	}
	
	@Override
	protected void doPickupItem(L2Object object)
	{
		getAI().setIntention(Intention.AI_INTENTION_IDLE);
		StopMove sm = new StopMove(getObjectId(), getX(), getY(), getZ(), getHeading());
		
		if (Config.DEBUG)
		{
			_logPet.debug("Pet pickup pos: " + object.getX() + " " + object.getY() + " " + object.getZ());
		}
		
		broadcastPacket(sm);
		
		if (!(object instanceof L2ItemInstance))
		{
			// dont try to pickup anything that is not an item :)
			_logPet.warn("trying to pickup wrong target." + object);
			getOwner().sendPacket(new ActionFailed());
			return;
		}
		
		L2ItemInstance target = (L2ItemInstance) object;
		
		// Herbs
		if ((target.getId() > 8599) && (target.getId() < 8615))
		{
			SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1);
			smsg.addItemName(target.getId());
			getOwner().sendPacket(smsg);
			return;
		}
		// Cursed weapons
		if (CursedWeaponsManager.getInstance().isCursed(target.getId()))
		{
			SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1);
			smsg.addItemName(target.getId());
			getOwner().sendPacket(smsg);
			return;
		}
		
		synchronized (target)
		{
			if (!target.isVisible())
			{
				getOwner().sendPacket(new ActionFailed());
				return;
			}
			
			if ((target.getOwnerId() != 0) && (target.getOwnerId() != getOwner().getObjectId()) && !getOwner().isInLooterParty(target.getOwnerId()))
			{
				getOwner().sendPacket(new ActionFailed());
				
				if (target.getId() == 57)
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA);
					smsg.addNumber(target.getCount());
					getOwner().sendPacket(smsg);
				}
				else if (target.getCount() > 1)
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S);
					smsg.addItemName(target.getId());
					smsg.addNumber(target.getCount());
					getOwner().sendPacket(smsg);
				}
				else
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1);
					smsg.addItemName(target.getId());
					getOwner().sendPacket(smsg);
				}
				
				return;
			}
			if ((target.getItemLootSchedule() != null) && ((target.getOwnerId() == getOwner().getObjectId()) || getOwner().isInLooterParty(target.getOwnerId())))
			{
				target.resetOwnerTimer();
			}
			
			target.pickupMe(this);
			
			if (Config.SAVE_DROPPED_ITEM)
			{
				ItemsOnGroundManager.getInstance().removeObject(target);
			}
		}
		
		getInventory().addItem("Pickup", target, getOwner(), this);
		// FIXME Just send the updates if possible (old way wasn't working though)
		PetItemList iu = new PetItemList(this);
		getOwner().sendPacket(iu);
		
		getAI().setIntention(Intention.AI_INTENTION_IDLE);
		
		if (getFollowStatus())
		{
			followOwner();
		}
	}
	
	@Override
	public void deleteMe(L2PcInstance owner)
	{
		super.deleteMe(owner);
		destroyControlItem(owner); // this should also delete the pet from the db
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer, true))
		{
			return false;
		}
		stopFeed();
		DecayTaskManager.getInstance().addDecayTask(this, 1200000);
		deathPenalty();
		return true;
	}
	
	@Override
	public void doRevive()
	{
		if (_curFed > (getMaxFed() / 10))
		{
			_curFed = getMaxFed() / 10;
		}
		
		getOwner().removeReviving();
		
		super.doRevive();
		
		// stopDecay
		DecayTaskManager.getInstance().cancelDecayTask(this);
		startFeed(false);
	}
	
	@Override
	public void doRevive(double revivePower)
	{
		// Restore the pet's lost experience,
		// depending on the % return of the skill used (based on its power).
		restoreExp(revivePower);
		doRevive();
	}
	
	/**
	 * Transfers item to another inventory
	 * @param process : String Identifier of process triggering this action
	 * @param objectId object Id of the item to be transfered
	 * @param count : int Quantity of items to be transfered
	 * @param target
	 * @param actor : L2PcInstance Player requesting the item transfer
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public L2ItemInstance transferItem(String process, int objectId, int count, Inventory target, L2PcInstance actor, L2Object reference)
	{
		L2ItemInstance oldItem = getInventory().getItemByObjectId(objectId);
		L2ItemInstance newItem = getInventory().transferItem(process, objectId, count, target, actor, reference);
		
		if (newItem == null)
		{
			return null;
		}
		
		// Send inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		if ((oldItem.getCount() > 0) && (oldItem != newItem))
		{
			petIU.addModifiedItem(oldItem);
		}
		else
		{
			petIU.addRemovedItem(oldItem);
		}
		getOwner().sendPacket(petIU);
		
		// Send target update packet
		if (target instanceof PcInventory)
		{
			L2PcInstance targetPlayer = ((PcInventory) target).getOwner();
			InventoryUpdate playerUI = new InventoryUpdate();
			if (newItem.getCount() > count)
			{
				playerUI.addModifiedItem(newItem);
			}
			else
			{
				playerUI.addNewItem(newItem);
			}
			targetPlayer.sendPacket(playerUI);
			
			// Update current load as well
			StatusUpdate playerSU = new StatusUpdate(targetPlayer.getObjectId());
			playerSU.addAttribute(StatusUpdate.CUR_LOAD, targetPlayer.getCurrentLoad());
			targetPlayer.sendPacket(playerSU);
		}
		else if (target instanceof PetInventory)
		{
			petIU = new PetInventoryUpdate();
			if (newItem.getCount() > count)
			{
				petIU.addRemovedItem(newItem);
			}
			else
			{
				petIU.addNewItem(newItem);
			}
			((PetInventory) target).getOwner().getOwner().sendPacket(petIU);
		}
		return newItem;
	}
	
	@Override
	public void giveAllToOwner()
	{
		try
		{
			Inventory petInventory = getInventory();
			L2ItemInstance[] items = petInventory.getItems();
			for (int i = 0; (i < items.length); i++)
			{
				L2ItemInstance giveit = items[i];
				if (((giveit.getWeight() * giveit.getCount()) + getOwner().getInventory().getTotalWeight()) < getOwner().getMaxLoad())
				{
					// If the owner can carry it give it to them
					giveItemToOwner(giveit);
				}
				else
				{
					// If they can't carry it, chuck it on the floor :)
					dropItemHere(giveit);
				}
			}
		}
		catch (Exception e)
		{
			_logPet.warn("Give allTemplates items error " + e);
		}
	}
	
	public void giveItemToOwner(L2ItemInstance item)
	{
		try
		{
			getInventory().transferItem("PetTransfer", item.getObjectId(), item.getCount(), getOwner().getInventory(), getOwner(), this);
			PetInventoryUpdate petiu = new PetInventoryUpdate();
			ItemListPacket PlayerUI = new ItemListPacket(getOwner(), false);
			petiu.addRemovedItem(item);
			getOwner().sendPacket(petiu);
			getOwner().sendPacket(PlayerUI);
		}
		catch (Exception e)
		{
			_logPet.warn("Error while giving item to owner: " + e);
		}
	}
	
	/**
	 * Remove the Pet from DB and its associated item from the reader inventory
	 * @param owner The owner from whose invenory we should delete the item
	 */
	public void destroyControlItem(L2PcInstance owner)
	{
		// remove the pet instance from world
		L2World.getInstance().removePet(owner.getObjectId());
		
		// delete from inventory
		try
		{
			L2ItemInstance removedItem = owner.getInventory().destroyItem("PetDestroy", getControlItemId(), 1, getOwner(), this);
			
			InventoryUpdate iu = new InventoryUpdate();
			iu.addRemovedItem(removedItem);
			
			owner.sendPacket(iu);
			
			StatusUpdate su = new StatusUpdate(owner.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, owner.getCurrentLoad());
			owner.sendPacket(su);
			
			owner.broadcastUserInfo();
			
			L2World world = L2World.getInstance();
			world.removeObject(removedItem);
		}
		catch (Exception e)
		{
			_logPet.warn("Error while destroying control item: " + e);
		}

        PetsRepository repository = DatabaseAccess.getRepository(PetsRepository.class);
		repository.deleteById(getControlItemId());
	}
	
	public void dropAllItems()
	{
		try
		{
			L2ItemInstance[] items = getInventory().getItems();
			for (int i = 0; (i < items.length); i++)
			{
				dropItemHere(items[i]);
			}
		}
		catch (Exception e)
		{
			_logPet.warn("Pet Drop Error: " + e);
		}
	}
	
	public void dropItemHere(L2ItemInstance dropit)
	{
		dropit = getInventory().dropItem("Drop", dropit.getObjectId(), dropit.getCount(), getOwner(), this);
		
		if (dropit != null)
		{
			_logPet.debug("Item id to drop: " + dropit.getId() + " amount: " + dropit.getCount());
			dropit.dropMe(this, getX(), getY(), getZ() + 100);
		}
	}
	
	// public void startAttack(L2Character target)
	// {
	// if (!knownsObject(target))
	// {
	// target.addKnownObject(this);
	// this.addKnownObject(target);
	// }
	// if (!target.knownsObject(this))
	// {
	// target.addKnownObject(this);
	// this.addKnownObject(target);
	// }
	//
	// if (!isRunning())
	// {
	// setRunning(true);
	// ChangeMoveType move = new ChangeMoveType(this, ChangeMoveType.RUN);
	// broadcastPacket(move);
	// }
	//
	// super.startAttack(target);
	// }
	//
	/** @return Returns the mountable. */
	@Override
	public boolean isMountable()
	{
		return _mountable;
	}
	
	private static L2PetInstance restore(L2ItemInstance control, NpcTemplate template, L2PcInstance owner) {
        L2PetInstance pet;
        if (L2BabyPet.equals(template.getType())) {
            pet = new L2BabyPetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
        }
        else {
            pet = new L2PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
        }

        PetsRepository repository = DatabaseAccess.getRepository(PetsRepository.class);
        repository.findById(control.getObjectId()).ifPresent(petData -> {
            pet._respawned = true;
            pet.setName(petData.getName());

            pet.getStat().setLevel(petData.getLevel());
            pet.getStat().setExp(petData.getExp());
            pet.getStat().setSp(petData.getSp());

            pet.getStatus().setCurrentHp(petData.getCurHp());
            pet.getStatus().setCurrentMp(petData.getCurMp());
            pet.getStatus().setCurrentCp(pet.getMaxCp());

            pet.setKarma(petData.getKarma());
            pet.setPkKills(petData.getPkkills());
            pet.setCurrentFed(petData.getFed());
        });
        return pet;
	}
	
	@Override
	public void store()
	{
		if (getControlItemId() == 0)
		{
			// this is a summon, not a pet, don't store anything
			return;
		}
		
		String req;
        PetsRepository repository = DatabaseAccess.getRepository(PetsRepository.class);
		if (!isRespawned()) {
		    Pets pet = new Pets(getControlItemId(), getName(), getStat().getLevel(), getStatus().getCurrentHp(), getStatus().getCurrentMp(), getStat().getExp(),
                    getStat().getSp(), getKarma(), getPkKills(), getCurrentFed());
		    repository.save(pet);
		}
		else {
		    repository.updateById(getControlItemId(), getName(), getStat().getLevel(), getStatus().getCurrentHp(), getStatus().getCurrentMp(),
                    getStat().getExp(), getStat().getSp(), getKarma(), getPkKills(), getCurrentFed());
		}
		
		L2ItemInstance itemInst = getControlItem();
		if ((itemInst != null) && (itemInst.getEnchantLevel() != getStat().getLevel()))
		{
			itemInst.setEnchantLevel(getStat().getLevel());
			itemInst.updateDatabase();
		}
	}
	
	public synchronized void stopFeed()
	{
		if (_feedTask != null)
		{
			_feedTask.cancel(false);
			_feedTask = null;
			if (Config.DEBUG)
			{
				_logPet.debug("Pet [#" + getObjectId() + "] feed task stop");
			}
		}
	}
	
	public synchronized void startFeed(boolean battleFeed)
	{
		// stop feeding task if its active
		
		stopFeed();
		if (!isDead())
		{
			if (battleFeed)
			{
				_feedMode = true;
				_feedTime = _data.getFeedbattle();
			}
			else
			{
				_feedMode = false;
				_feedTime = _data.getFeednormal();
			}
			// pet feed time must be different than 0. Changing time to bypass divide by 0
			if (_feedTime <= 0)
			{
				_feedTime = 1;
			}
			
			_feedTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new FeedTask(), 60000 / _feedTime, 60000 / _feedTime);
		}
	}
	
	@Override
	public synchronized void unSummon(L2PcInstance owner)
	{
		stopFeed();
		stopHpMpRegeneration();
		super.unSummon(owner);
		
		if (!isDead())
		{
			L2World.getInstance().removePet(owner.getObjectId());
		}
	}
	
	/**
	 * Restore the specified % of experience this L2PetInstance has lost.<BR>
	 * <BR>
	 * @param restorePercent
	 */
	public void restoreExp(double restorePercent)
	{
		if (_expBeforeDeath > 0)
		{
			// Restore the specified % of lost experience.
			getStat().addExp(Math.round(((_expBeforeDeath - getStat().getExp()) * restorePercent) / 100));
			_expBeforeDeath = 0;
		}
	}
	
	private void deathPenalty()
	{
		// TODO Need Correct Penalty
		
		int lvl = getStat().getLevel();
		double percentLost = (-0.07 * lvl) + 6.5;
		
		// Calculate the Experience loss
		long lostExp = Math.round(((getStat().getExpForLevel(lvl + 1) - getStat().getExpForLevel(lvl)) * percentLost) / 100);
		
		// Get the Experience before applying penalty
		_expBeforeDeath = getStat().getExp();
		
		// Set the new Experience value of the L2PetInstance
		getStat().addExp(-lostExp);
	}
	
	@Override
	public void addExpAndSp(long addToExp, int addToSp)
	{
		if (getNpcId() == 12564)
		{
			getStat().addExpAndSp(Math.round(addToExp * Config.SINEATER_XP_RATE), addToSp);
		}
		else
		{
			getStat().addExpAndSp(Math.round(addToExp * Config.PET_XP_RATE), addToSp);
		}
	}
	
	@Override
	public long getExpForThisLevel()
	{
		return getStat().getExpForLevel(getLevel());
	}
	
	@Override
	public long getExpForNextLevel()
	{
		return getStat().getExpForLevel(getLevel() + 1);
	}
	
	@Override
	public final int getLevel()
	{
		return getStat().getLevel();
	}
	
	public int getMaxFed()
	{
		return getStat().getMaxFeed();
	}
	
	@Override
	public int getAccuracy()
	{
		return getStat().getAccuracy();
	}
	
	@Override
	public int getCriticalHit(L2Character target, L2Skill skill)
	{
		return getStat().getCriticalHit(target, skill);
	}
	
	@Override
	public int getEvasionRate(L2Character target)
	{
		return getStat().getEvasionRate(target);
	}
	
	@Override
	public int getRunSpeed()
	{
		return getStat().getRunSpeed();
	}
	
	@Override
	public int getPAtkSpd()
	{
		return getStat().getPAtkSpd();
	}
	
	@Override
	public int getMAtkSpd()
	{
		return getStat().getMAtkSpd();
	}
	
	@Override
	public int getMAtk(L2Character target, L2Skill skill)
	{
		return getStat().getMAtk(target, skill);
	}
	
	@Override
	public int getMDef(L2Character target, L2Skill skill)
	{
		return getStat().getMDef(target, skill);
	}
	
	@Override
	public int getPAtk(L2Character target)
	{
		return getStat().getPAtk(target);
	}
	
	@Override
	public int getPDef(L2Character target)
	{
		return getStat().getPDef(target);
	}
	
	@Override
	public final int getSkillLevel(int skillId)
	{
		if ((skills == null) || (skills.get(skillId) == null))
		{
			return -1;
		}
		int lvl = getLevel();
		return lvl > 70 ? 7 + ((lvl - 70) / 5) : lvl / 10;
	}
	
	public void updateRefOwner(L2PcInstance owner)
	{
		int oldOwnerId = getOwner().getObjectId();
		
		setOwner(owner);
		L2World.getInstance().removePet(oldOwnerId);
		L2World.getInstance().addPet(oldOwnerId, this);
	}
	
	@Override
	public final void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss)
		{
			return;
		}
		
		// Prevents the double spam of system messages, if the target is the owning reader.
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.CRITICAL_HIT_BY_PET));
			}
			
			SystemMessage sm = new SystemMessage(SystemMessageId.PET_HIT_FOR_S1_DAMAGE);
			sm.addNumber(damage);
			getOwner().sendPacket(sm);
		}
	}
}