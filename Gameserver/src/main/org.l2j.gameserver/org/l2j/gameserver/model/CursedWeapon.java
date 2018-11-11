package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.position.Position;
import org.l2j.gameserver.model.entity.database.CursedWeapons;
import org.l2j.gameserver.model.entity.database.repository.CharacterRepository;
import org.l2j.gameserver.model.entity.database.repository.CursedWeaponRepository;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.templates.xml.jaxb.BodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;


public class CursedWeapon
{
	private static final Logger _log = LoggerFactory.getLogger(CursedWeaponsManager.class.getName());
	
	private final String _name;
	private final int _itemId;
	private final int _skillId;
	private final int _skillMaxLevel;
	private int _dropRate;
	private int _duration;
	private int _durationLost;
	private int _disapearChance;
	private int _stageKills;
	
	private boolean _isDropped = false;
	private boolean _isActivated = false;
	private ScheduledFuture<?> _removeTask;
	
	private int _nbKills = 0;
	private long _endTime = 0;
	
	private int _playerId = 0;
	private L2PcInstance player = null;
	private L2ItemInstance item = null;
	private int _playerKarma = 0;
	private int _playerPkKills = 0;
	
	// =========================================================
	// Constructor
	public CursedWeapon(int itemId, int skillId, String name)
	{
		_name = name;
		_itemId = itemId;
		_skillId = skillId;
		_skillMaxLevel = SkillTable.getInstance().getMaxLevel(_skillId, 0);
	}
	
	// =========================================================
	// Private
	public void endOfLife()
	{
		if (_isActivated)
		{
			if ((player != null) && (player.isOnline()))
			{
				// Remove from reader
				_log.info(_name + " being removed online.");
				
				player.abortAttack();
				
				player.setKarma(_playerKarma);
				player.setPkKills(_playerPkKills);
				player.setCursedWeaponEquipedId(0);
				removeSkill();
				
				// Remove
				player.getInventory().unEquipItemInBodySlotAndRecord(BodyPart.TWO_HANDS);
				player.store();
				
				// Destroy
				L2ItemInstance removedItem = player.getInventory().destroyItemByItemId("", _itemId, 1, player, null);
				if (!Config.FORCE_INVENTORY_UPDATE)
				{
					InventoryUpdate iu = new InventoryUpdate();
					if (removedItem.getCount() == 0)
					{
						iu.addRemovedItem(removedItem);
					}
					else
					{
						iu.addModifiedItem(removedItem);
					}
					
					player.sendPacket(iu);
				}
				else
				{
					player.sendPacket(new ItemListPacket(player, true));
				}
				
				player.broadcastUserInfo();
			}
			else {
				// Remove from Db
				_log.info("{} being removed offline.", _name);
                CharacterRepository characterRepository = DatabaseAccess.getRepository(CharacterRepository.class);
                if(characterRepository.updatePKAndKarma(_playerId, _playerPkKills, _playerKarma) < 1) {
                    _log.warn("Error while updating karma & pkkills for userId {}",  _playerId);
                }

                ItemRepository itemRepository = DatabaseAccess.getRepository(ItemRepository.class);
                if(itemRepository.deleteByOwnerAndItem(_playerId, _itemId) < 1) {
                    _log.warn("Error while deleting itemId {} from userId {}", _itemId, _playerId);
                }
			}
		}
		else
		{
			// either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
			// OR this cursed weapon is on the ground.
			if ((player != null) && (player.getInventory().getItemByItemId(_itemId) != null))
			{
				// Destroy
				L2ItemInstance removedItem = player.getInventory().destroyItemByItemId("", _itemId, 1, player, null);
				if (!Config.FORCE_INVENTORY_UPDATE)
				{
					InventoryUpdate iu = new InventoryUpdate();
					if (removedItem.getCount() == 0)
					{
						iu.addRemovedItem(removedItem);
					}
					else
					{
						iu.addModifiedItem(removedItem);
					}
					
					player.sendPacket(iu);
				}
				else
				{
					player.sendPacket(new ItemListPacket(player, true));
				}
				
				player.broadcastUserInfo();
			}
			// is dropped on the ground
			else if (item != null)
			{
				item.decayMe();
				L2World.getInstance().removeObject(item);
				_log.info(_name + " item has been removed from World.");
			}
		}
		
		// Delete infos from table if any
		CursedWeaponsManager.removeFromDb(_itemId);
		
		SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_DISAPPEARED);
		sm.addItemName(_itemId);
		CursedWeaponsManager.announce(sm);
		
		// Reset state
		cancelTask();
		_isActivated = false;
		_isDropped = false;
		_endTime = 0;
		player = null;
		_playerId = 0;
		_playerKarma = 0;
		_playerPkKills = 0;
		item = null;
		_nbKills = 0;
	}
	
	private void cancelTask()
	{
		if (_removeTask != null)
		{
			_removeTask.cancel(true);
			_removeTask = null;
		}
	}
	
	private class RemoveTask implements Runnable
	{
		protected RemoveTask()
		{
		}
		
		@Override
		public void run()
		{
			if (System.currentTimeMillis() >= getEndTime())
			{
				endOfLife();
			}
		}
	}
	
	private void dropIt(L2Attackable attackable, L2PcInstance player)
	{
		dropIt(attackable, player, null, true);
	}
	
	private void dropIt(L2Attackable attackable, L2PcInstance player, L2Character killer, boolean fromMonster)
	{
		_isActivated = false;
		
		if (fromMonster)
		{
			item = attackable.DropItem(player, _itemId, 1);
			item.setDropTime(0); // Prevent item from being removed by ItemsAutoDestroy
			
			// RedSky and Earthquake
			ExRedSky packet = new ExRedSky(10);
			Earthquake eq = new Earthquake(player.getX(), player.getY(), player.getZ(), 14, 3);
			for (L2PcInstance aPlayer : L2World.getInstance().getAllPlayers())
			{
				aPlayer.sendPacket(packet);
				aPlayer.sendPacket(eq);
			}
		}
		else
		{
			this.player.dropItem("DieDrop", item, killer, true);
			this.player.setKarma(_playerKarma);
			this.player.setPkKills(_playerPkKills);
			this.player.setCursedWeaponEquipedId(0);
			removeSkill();
			this.player.abortAttack();
			// L2ItemInstance item = player.getInventory().getItemByItemId(_itemId);
			// player.getInventory().dropItem("DieDrop", item, player, null);
			// player.getInventory().getItemByItemId(_itemId).dropMe(player, player.getX(), player.getY(), player.getZ());
		}
		
		_isDropped = true;
		SystemMessage sm = new SystemMessage(SystemMessageId.S2_WAS_DROPPED_IN_THE_S1_REGION);
		if (player != null)
		{
			sm.addZoneName(player.getX(), player.getY(), player.getZ()); // Region Name
		}
		else if (this.player != null)
		{
			sm.addZoneName(this.player.getX(), this.player.getY(), this.player.getZ()); // Region Name
		}
		else
		{
			sm.addZoneName(killer.getX(), killer.getY(), killer.getZ()); // Region Name
		}
		sm.addItemName(_itemId);
		CursedWeaponsManager.announce(sm); // in the Hot Spring region
	}
	
	/**
	 * Yesod:<br>
	 * Rebind the passive skill belonging to the CursedWeapon. Invoke this method if the weapon owner switches to a subclass.
	 */
	public void giveSkill()
	{
		int level = 1 + (_nbKills / _stageKills);
		if (level > _skillMaxLevel)
		{
			level = _skillMaxLevel;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, level);
		// Yesod:
		// To properly support subclasses this skill can not be stored.
		player.addSkill(skill, false);
		
		// Void Burst, Void Flow
		skill = SkillTable.getInstance().getInfo(3630, 1);
		player.addSkill(skill, false);
		skill = SkillTable.getInstance().getInfo(3631, 1);
		player.addSkill(skill, false);
		
		if (Config.DEBUG)
		{
			System.out.println("Player " + player.getName() + " has been awarded with skill " + skill);
		}
		player.sendSkillList();
	}
	
	public void removeSkill()
	{
		player.removeSkill(SkillTable.getInstance().getInfo(_skillId, player.getSkillLevel(_skillId)), false);
		player.removeSkill(SkillTable.getInstance().getInfo(3630, 1), false);
		player.removeSkill(SkillTable.getInstance().getInfo(3631, 1), false);
		player.sendSkillList();
	}
	
	// =========================================================
	// Public
	public void reActivate()
	{
		_isActivated = true;
		if ((_endTime - System.currentTimeMillis()) <= 0)
		{
			endOfLife();
		}
		else
		{
			_removeTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new RemoveTask(), _durationLost * 12000L, _durationLost * 12000L);
		}
		
	}
	
	public boolean checkDrop(L2Attackable attackable, L2PcInstance player)
	{
		if (Rnd.get(100000) < _dropRate)
		{
			// Drop the item
			dropIt(attackable, player);
			
			// Start the Life Task
			_endTime = System.currentTimeMillis() + (_duration * 60000L);
			_removeTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new RemoveTask(), _durationLost * 12000L, _durationLost * 12000L);
			
			return true;
		}
		
		return false;
	}
	
	public void activate(L2PcInstance player, L2ItemInstance item)
	{
		// if the reader is mounted, attempt to unmount first. Only allow picking up
		// the zariche if unmounting is successful.
		if (player.isMounted())
		{
			if (this.player.setMountType(0))
			{
				Ride dismount = new Ride(this.player.getObjectId(), Ride.ACTION_DISMOUNT, 0);
				this.player.broadcastPacket(dismount);
				this.player.setMountObjectID(0);
			}
			else
			{
				// TODO: correct this custom message.
				player.sendMessage("You may not pick up this item while riding in this territory");
				return;
			}
		}
		
		_isActivated = true;
		
		// Player holding it data
		this.player = player;
		_playerId = this.player.getObjectId();
		_playerKarma = this.player.getKarma();
		_playerPkKills = this.player.getPkKills();
		saveData();
		
		// Change reader stats
		this.player.setCursedWeaponEquipedId(_itemId);
		this.player.setKarma(9000000);
		this.player.setPkKills(0);
		if (this.player.isInParty())
		{
			this.player.getParty().oustPartyMember(this.player);
		}
		
		// Add skill
		giveSkill();
		
		// Equip with the weapon
		this.item = item;
		// L2ItemInstance[] items =
		this.player.getInventory().equipItemAndRecord(this.item);
		SystemMessage sm = new SystemMessage(SystemMessageId.S1_EQUIPPED);
		sm.addItemName(this.item.getId());
		this.player.sendPacket(sm);
		
		// Fully heal reader
		this.player.setCurrentHpMp(this.player.getMaxHp(), this.player.getMaxMp());
		this.player.setCurrentCp(this.player.getMaxCp());
		
		// Refresh inventory
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(this.item);
			// iu.addItems(Arrays.asList(items));
			this.player.sendPacket(iu);
		}
		else
		{
			this.player.sendPacket(new ItemListPacket(this.player, false));
		}
		
		// Refresh reader stats
		this.player.broadcastUserInfo();
		
		SocialAction atk = new SocialAction(this.player.getObjectId(), 17);
		
		this.player.broadcastPacket(atk);
		
		sm = new SystemMessage(SystemMessageId.THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION);
		sm.addZoneName(this.player.getX(), this.player.getY(), this.player.getZ()); // Region Name
		sm.addItemName(this.item.getId());
		CursedWeaponsManager.announce(sm);
	}
	
	public void saveData() {
        CursedWeaponRepository repository = DatabaseAccess.getRepository(CursedWeaponRepository.class);
        repository.deleteById(_itemId);

        if (_isActivated) {
            CursedWeapons weapon = new CursedWeapons(_itemId, _playerId, _playerKarma, _playerPkKills, _nbKills, _endTime);
            repository.save(weapon);
        }
	}
	
	public void dropIt(L2Character killer)
	{
		if (Rnd.get(100) <= _disapearChance)
		{
			// Remove it
			endOfLife();
		}
		else
		{
			// Unequip & Drop
			dropIt(null, null, killer, false);
			// Reset reader stats
			player.setKarma(_playerKarma);
			player.setPkKills(_playerPkKills);
			player.setCursedWeaponEquipedId(0);
			removeSkill();
			
			player.abortAttack();
			
			// Unequip weapon
			// player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LRHAND);
			
			player.broadcastUserInfo();
		}
	}
	
	public void increaseKills()
	{
		_nbKills++;
		
		player.setPkKills(_nbKills);
		player.broadcastUserInfo();
		
		if (((_nbKills % _stageKills) == 0) && (_nbKills <= (_stageKills * (_skillMaxLevel - 1))))
		{
			giveSkill();
		}
		
		// Reduce time-to-live
		_endTime -= _durationLost * 60000L;
		saveData();
	}
	
	// =========================================================
	// Setter
	public void setDisapearChance(int disapearChance)
	{
		_disapearChance = disapearChance;
	}
	
	public void setDropRate(int dropRate)
	{
		_dropRate = dropRate;
	}
	
	public void setDuration(int duration)
	{
		_duration = duration;
	}
	
	public void setDurationLost(int durationLost)
	{
		_durationLost = durationLost;
	}
	
	public void setStageKills(int stageKills)
	{
		_stageKills = stageKills;
	}
	
	public void setNbKills(int nbKills)
	{
		_nbKills = nbKills;
	}
	
	public void setPlayerId(int playerId)
	{
		_playerId = playerId;
	}
	
	public void setPlayerKarma(int playerKarma)
	{
		_playerKarma = playerKarma;
	}
	
	public void setPlayerPkKills(int playerPkKills)
	{
		_playerPkKills = playerPkKills;
	}
	
	public void setActivated(boolean isActivated)
	{
		_isActivated = isActivated;
	}
	
	public void setDropped(boolean isDropped)
	{
		_isDropped = isDropped;
	}
	
	public void setEndTime(long endTime)
	{
		_endTime = endTime;
	}
	
	public void setPlayer(L2PcInstance player)
	{
		this.player = player;
	}
	
	public void setItem(L2ItemInstance item)
	{
		this.item = item;
	}
	
	// =========================================================
	// Getter
	public boolean isActivated()
	{
		return _isActivated;
	}
	
	public boolean isDropped()
	{
		return _isDropped;
	}
	
	public long getEndTime()
	{
		return _endTime;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public L2PcInstance getPlayer()
	{
		return player;
	}
	
	public int getPlayerKarma()
	{
		return _playerKarma;
	}
	
	public int getPlayerPkKills()
	{
		return _playerPkKills;
	}
	
	public int getNbKills()
	{
		return _nbKills;
	}
	
	public int getStageKills()
	{
		return _stageKills;
	}
	
	public boolean isActive()
	{
		return _isActivated || _isDropped;
	}
	
	public int getLevel()
	{
		if (_nbKills > (_stageKills * _skillMaxLevel))
		{
			return _skillMaxLevel;
		}
		return (_nbKills / _stageKills);
	}
	
	public long getTimeLeft()
	{
		return _endTime - System.currentTimeMillis();
	}
	
	public void goTo(L2PcInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		if (_isActivated)
		{
			// Go to reader holding the weapon
			player.teleToLocation(this.player.getX(), this.player.getY(), this.player.getZ() + 20, true);
		}
		else if (_isDropped)
		{
			// Go to item on the ground
			player.teleToLocation(item.getX(), item.getY(), item.getZ() + 20, true);
		}
		else
		{
			player.sendMessage(_name + " isn't in the World.");
		}
	}


	public Position getPosition() {
        if (_isActivated && (nonNull(player))) {
            return player.getPosition();
        }

        if (_isDropped && nonNull(item)) {
            return item.getPosition();
        }
        return null;
    }
}
