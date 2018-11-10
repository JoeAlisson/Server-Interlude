package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Manor;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2ManorManagerInstance;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.CropProcure;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.Item;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.util.Util;

/**
 * Format: (ch) d [dddd] d: size [ d obj id d item id d manor id d count ]
 * @author l3x
 */
public class RequestProcureCropList extends L2GameClientPacket
{
	private static final String _C__D0_09_REQUESTPROCURECROPLIST = "[C] D0:09 RequestProcureCropList";
	
	private int _size;
	
	private int[] _items; // count*4
	
	@Override
	protected void readImpl()
	{
		_size = readInt();
		if (((_size * 16) > availableData()) || (_size > 500))
		{
			_size = 0;
			return;
		}
		_items = new int[_size * 4];
		for (int i = 0; i < _size; i++)
		{
			int objId = readInt();
			_items[(i * 4) + 0] = objId;
			int itemId = readInt();
			_items[(i * 4) + 1] = itemId;
			int manorId = readInt();
			_items[(i * 4) + 2] = manorId;
			long count = readInt();
			if (count > Integer.MAX_VALUE)
			{
				count = Integer.MAX_VALUE;
			}
			_items[(i * 4) + 3] = (int) count;
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		L2Object target = player.getTarget();
		
		if (!(target instanceof L2ManorManagerInstance))
		{
			target = player.getLastFolkNPC();
		}
		
		if (!player.isGM() && ((target == null) || !(target instanceof L2ManorManagerInstance) || !player.isInsideRadius(target, L2NpcInstance.INTERACTION_DISTANCE, false, false)))
		{
			return;
		}
		
		if (_size < 1)
		{
			sendPacket(new ActionFailed());
			return;
		}
		L2ManorManagerInstance manorManager = (L2ManorManagerInstance) target;
		
		int currentManorId = manorManager.getCastle().getCastleId();
		
		// Calculate summary values
		int slots = 0;
		int weight = 0;
		
		for (int i = 0; i < _size; i++)
		{
			int itemId = _items[(i * 4) + 1];
			int manorId = _items[(i * 4) + 2];
			int count = _items[(i * 4) + 3];
			
			if ((itemId == 0) || (manorId == 0) || (count == 0))
			{
				continue;
			}
			if (count < 1)
			{
				continue;
			}
			if (count > Integer.MAX_VALUE)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.", Config.DEFAULT_PUNISH);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				return;
			}
			
			try
			{
				CropProcure crop = CastleManager.getInstance().getCastleById(manorId).getCrop(itemId, CastleManorManager.PERIOD_CURRENT);
				int rewardItemId = L2Manor.getInstance().getRewardItem(itemId, crop.getReward());
				ItemTemplate template = ItemTable.getInstance().getTemplate(rewardItemId);
				weight += count * template.getWeight();
				
				if (!(template instanceof Item) || !((Item)template).isStackable())
				{
					slots += count;
				}
				else if (player.getInventory().getItemByItemId(itemId) == null)
				{
					slots++;
				}
			}
			catch (NullPointerException e)
			{
				continue;
			}
		}
		
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		// Proceed the purchase
		InventoryUpdate playerIU = new InventoryUpdate();
		
		for (int i = 0; i < _size; i++)
		{
			int objId = _items[(i * 4) + 0];
			int cropId = _items[(i * 4) + 1];
			int manorId = _items[(i * 4) + 2];
			int count = _items[(i * 4) + 3];
			
			if ((objId == 0) || (cropId == 0) || (manorId == 0) || (count == 0))
			{
				continue;
			}
			
			if (count < 1)
			{
				continue;
			}

			CropProcure crop = null;
			
			try
			{
				crop = CastleManager.getInstance().getCastleById(manorId).getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
			}
			catch (NullPointerException e)
			{
				continue;
			}
			if ((crop == null) || (crop.getCropId() == 0) || (crop.getPrice() == 0))
			{
				continue;
			}
			
			long fee = 0; // fee for selling to other manors
			
			int rewardItem = L2Manor.getInstance().getRewardItem(cropId, crop.getReward());
			
			if (count > crop.getAmount())
			{
				continue;
			}
			
			long sellPrice = (count * L2Manor.getInstance().getCropBasicPrice(cropId));
			long rewardPrice = ItemTable.getInstance().getTemplate(rewardItem).getPrice();
			
			if (rewardPrice == 0)
			{
				continue;
			}
			
			long rewardItemCount = sellPrice / rewardPrice;
			if (rewardItemCount < 1)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.FAILED_IN_TRADING_S2_OF_CROP_S1);
				sm.addItemName(cropId);
				sm.addNumber(count);
				player.sendPacket(sm);
				continue;
			}
			
			if (manorId != currentManorId)
			{
				fee = (sellPrice * 5) / 100; // 5% fee for selling to other manor
			}
			
			if (player.getInventory().getAdena() < fee)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.FAILED_IN_TRADING_S2_OF_CROP_S1);
				sm.addItemName(cropId);
				sm.addNumber(count);
				player.sendPacket(sm);
				sm = new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				player.sendPacket(sm);
				continue;
			}
			
			// Add item to Inventory and adjust update packet
			L2ItemInstance itemDel = null;
			L2ItemInstance itemAdd = null;
			if (player.getInventory().getItemByObjectId(objId) != null)
			{
				// check if reader have correct items count
				L2ItemInstance item = player.getInventory().getItemByObjectId(objId);
				if (item.getCount() < count)
				{
					continue;
				}
				
				itemDel = player.getInventory().destroyItem("Manor", objId, count, player, manorManager);
				if (itemDel == null)
				{
					continue;
				}
				if (fee > 0)
				{
					player.getInventory().reduceAdena("Manor", fee, player, manorManager);
				}
				crop.setAmount(crop.getAmount() - count);
				if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
				{
					CastleManager.getInstance().getCastleById(manorId).updateCrop(crop.getCropId(), crop.getAmount(), CastleManorManager.PERIOD_CURRENT);
				}
				itemAdd = player.getInventory().addItem("Manor", rewardItem, rewardItemCount, player, manorManager);
			}
			else
			{
				continue;
			}
			
			if (itemAdd == null)
			{
				continue;
			}
			
			playerIU.addRemovedItem(itemDel);
			if (itemAdd.getCount() > rewardItemCount)
			{
				playerIU.addModifiedItem(itemAdd);
			}
			else
			{
				playerIU.addNewItem(itemAdd);
			}
			
			// Send System Messages
			SystemMessage sm = new SystemMessage(SystemMessageId.TRADED_S2_OF_CROP_S1);
			sm.addItemName(cropId);
			sm.addNumber(count);
			player.sendPacket(sm);
			
			if (fee > 0)
			{
				sm = new SystemMessage(SystemMessageId.S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES);
				sm.addNumber(fee);
				player.sendPacket(sm);
			}
			
			sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
			sm.addItemName(cropId);
			sm.addNumber(count);
			player.sendPacket(sm);
			
			if (fee > 0)
			{
				sm = new SystemMessage(SystemMessageId.DISSAPEARED_ADENA);
				sm.addNumber(fee);
				player.sendPacket(sm);
			}
			
			sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
			sm.addItemName(rewardItem);
			sm.addNumber(rewardItemCount);
			player.sendPacket(sm);
		}
		
		// Send update packets
		player.sendPacket(playerIU);
		
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		
	}
	
	@Override
	public String getType()
	{
		return _C__D0_09_REQUESTPROCURECROPLIST;
	}
}
