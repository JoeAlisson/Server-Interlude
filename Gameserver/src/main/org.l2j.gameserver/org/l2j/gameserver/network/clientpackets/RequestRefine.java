package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.AugmentationData;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExVariationResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.CrystalType;
import org.l2j.gameserver.util.Util;

import java.util.Objects;

/**
 * Format:(ch) dddd
 * @author -Wooden-
 */
public final class RequestRefine extends L2GameClientPacket
{
	private static final String _C__D0_2C_REQUESTREFINE = "[C] D0:2C RequestRefine";
	private int _targetItemObjId;
	private int _refinerItemObjId;
	private int _gemstoneItemObjId;
	private int _gemstoneCount;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readInt();
		_refinerItemObjId = readInt();
		_gemstoneItemObjId = readInt();
		_gemstoneCount = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		L2ItemInstance targetItem = (L2ItemInstance) L2World.getInstance().findObject(_targetItemObjId);
		L2ItemInstance refinerItem = (L2ItemInstance) L2World.getInstance().findObject(_refinerItemObjId);
		L2ItemInstance gemstoneItem = (L2ItemInstance) L2World.getInstance().findObject(_gemstoneItemObjId);
		
		if ((targetItem == null) || (refinerItem == null) || (gemstoneItem == null) ||
				(!Objects.equals(targetItem.getOwner(), activeChar) || (!Objects.equals(refinerItem.getOwner(), activeChar) || (!Objects.equals(gemstoneItem.getOwner(), activeChar) || (activeChar.getLevel() < 46))))) // must
																																																																									// be
																																																																									// lvl
																																																																									// 46
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(new SystemMessage(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS));
			return;
		}
		
		// unequip item
		if (targetItem.isEquipped())
		{
			activeChar.disarmWeapons();
		}
		
		if (TryAugmentItem(activeChar, targetItem, refinerItem, gemstoneItem))
		{
			int stat12 = 0x0000FFFF & targetItem.getAugmentation().getAugmentationId();
			int stat34 = targetItem.getAugmentation().getAugmentationId() >> 16;
			activeChar.sendPacket(new ExVariationResult(stat12, stat34, 1));
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED));
		}
		else
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(new SystemMessage(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS));
		}
	}
	
	boolean TryAugmentItem(L2PcInstance player, L2ItemInstance targetItem, L2ItemInstance refinerItem, L2ItemInstance gemstoneItem)
	{
		if (targetItem.isAugmented() || targetItem.isWear())
		{
			return false;
		}
		
		// check for the items to be in the inventory of the owner
		if (player.getInventory().getItemByObjectId(refinerItem.getObjectId()) == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to refine an item with wrong LifeStone-id.", Config.DEFAULT_PUNISH);
			return false;
		}
		if (player.getInventory().getItemByObjectId(targetItem.getObjectId()) == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to refine an item with wrong Weapon-id.", Config.DEFAULT_PUNISH);
			return false;
		}
		if (player.getInventory().getItemByObjectId(gemstoneItem.getObjectId()) == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to refine an item with wrong Gemstone-id.", Config.DEFAULT_PUNISH);
			return false;
		}
		
		CrystalType itemGrade = targetItem.getCrystal();
		int lifeStoneId = refinerItem.getId();
		int gemstoneItemId = gemstoneItem.getId();
		
		// is the refiner Item a life stone?
		if ((lifeStoneId < 8723) || (lifeStoneId > 8762))
		{
			return false;
		}
		
		// must be a weapon, must be > d grade
		// TODO: can do better? : currently: using isdestroyable() as a check for hero / cursed weapons
		if ((itemGrade.compareTo(CrystalType.C) < 0) || targetItem.isWeapon() || !targetItem.isDestroyable())
		{
			return false;
		}
		
		// reader must be able to use augmentation
		if ((player.getPrivateStoreType() != L2PcInstance.STORE_PRIVATE_NONE) || player.isDead() || player.isParalyzed() || player.isFishing() || player.isSitting())
		{
			return false;
		}
		
		int modifyGemstoneCount = _gemstoneCount;
		int lifeStoneLevel = getLifeStoneLevel(lifeStoneId);
		int lifeStoneGrade = getLifeStoneGrade(lifeStoneId);
		switch (itemGrade)
		{
			case C:
				if ((player.getLevel() < 46) || (gemstoneItemId != 2130))
				{
					return false;
				}
				modifyGemstoneCount = 20;
				break;
			case B:
				if ((lifeStoneLevel < 3) || (player.getLevel() < 52) || (gemstoneItemId != 2130))
				{
					return false;
				}
				modifyGemstoneCount = 30;
				break;
			case A:
				if ((lifeStoneLevel < 6) || (player.getLevel() < 61) || (gemstoneItemId != 2131))
				{
					return false;
				}
				modifyGemstoneCount = 20;
				break;
			case S:
				if ((lifeStoneLevel != 10) || (player.getLevel() < 76) || (gemstoneItemId != 2131))
				{
					return false;
				}
				modifyGemstoneCount = 25;
				break;
		}
		
		// check if the lifestone is appropriate for this reader
		switch (lifeStoneLevel)
		{
			case 1:
				if (player.getLevel() < 46)
				{
					return false;
				}
				break;
			case 2:
				if (player.getLevel() < 49)
				{
					return false;
				}
				break;
			case 3:
				if (player.getLevel() < 52)
				{
					return false;
				}
				break;
			case 4:
				if (player.getLevel() < 55)
				{
					return false;
				}
				break;
			case 5:
				if (player.getLevel() < 58)
				{
					return false;
				}
				break;
			case 6:
				if (player.getLevel() < 61)
				{
					return false;
				}
				break;
			case 7:
				if (player.getLevel() < 64)
				{
					return false;
				}
				break;
			case 8:
				if (player.getLevel() < 67)
				{
					return false;
				}
				break;
			case 9:
				if (player.getLevel() < 70)
				{
					return false;
				}
				break;
			case 10:
				if (player.getLevel() < 76)
				{
					return false;
				}
				break;
		}
		
		if ((gemstoneItem.getCount() - modifyGemstoneCount) < 0)
		{
			return false;
		}
		
		// consume the life stone
		if (!player.destroyItem("RequestRefine", refinerItem, null, false))
		{
			return false;
		}
		
		// Prepare inventory update
		InventoryUpdate iu = new InventoryUpdate();
		
		if ((gemstoneItem.getCount() - modifyGemstoneCount) == 0)
		{
			player.destroyItem("RequestRefine", gemstoneItem, null, false);
			iu.addRemovedItem(gemstoneItem);
		}
		else
		{
			player.destroyItem("RequestRefine", _gemstoneItemObjId, modifyGemstoneCount, null, false);
			iu.addModifiedItem(gemstoneItem);
		}
		
		// generate augmentation
		targetItem.setAugmentation(AugmentationData.getInstance().generateRandomAugmentation(targetItem, lifeStoneLevel, lifeStoneGrade));
		
		// finish and send the inventory update packet
		iu.addModifiedItem(targetItem);
		iu.addRemovedItem(refinerItem);
		player.sendPacket(iu);
		
		return true;
	}
	
	private int getLifeStoneGrade(int itemId)
	{
		itemId -= 8723;
		if (itemId < 10)
		{
			return 0; // normal grade
		}
		if (itemId < 20)
		{
			return 1; // mid grade
		}
		if (itemId < 30)
		{
			return 2; // high grade
		}
		return 3; // top grade
	}
	
	private int getLifeStoneLevel(int itemId)
	{
		itemId -= 10 * getLifeStoneGrade(itemId);
		itemId -= 8722;
		return itemId;
	}
	
	@Override
	public String getType()
	{
		return _C__D0_2C_REQUESTREFINE;
	}
}