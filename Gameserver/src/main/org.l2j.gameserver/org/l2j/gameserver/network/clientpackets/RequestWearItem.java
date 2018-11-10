package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.TradeController;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2MercManagerInstance;
import org.l2j.gameserver.model.actor.instance.L2MerchantInstance;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.MerchantShop;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Future;

public final class RequestWearItem extends L2GameClientPacket
{
	private static final String _C__C6_REQUESTWEARITEM = "[C] C6 RequestWearItem";
	protected static final Logger _log = LoggerFactory.getLogger(RequestWearItem.class.getName());
	
	protected Future<?> _removeWearItemsTask;
	
	@SuppressWarnings("unused")
	private int _unknow;
	
	/** List of ItemID to Wear */
	private int _listId;
	
	/** Number of Item to Wear */
	private int _count;
	
	/** Table of ItemId containing allTemplates Item to Wear */
	private int[] _items;
	
	/** Player that request a Try on */
	protected L2PcInstance _activeChar;
	
	class RemoveWearItemsTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				_activeChar.destroyWearedItems("Wear", null, true);
				
			}
			catch (Throwable e)
			{
				_log.error( "", e);
			}
		}
	}
	
	/**
	 * Decrypt the RequestWearItem Client->Server Packet and Create _items table containing allTemplates ItemID to Wear.<BR>
	 * <BR>
	 */
	@Override
	protected void readImpl()
	{
		// Read and Decrypt the RequestWearItem Client->Server Packet
		_activeChar = getClient().getActiveChar();
		_unknow = readInt();
		_listId = readInt(); // List of ItemID to Wear
		_count = readInt(); // Number of Item to Wear
		
		if (_count < 0)
		{
			_count = 0;
		}
		if (_count > 100)
		{
			_count = 0; // prevent too long lists
		}
		
		// Create _items table that will contain allTemplates ItemID to Wear
		_items = new int[_count];
		
		// Fill _items table with allTemplates ItemID to Wear
		for (int i = 0; i < _count; i++)
		{
			int itemId = readInt();
			_items[i] = itemId;
		}
	}
	
	/**
	 * Launch Wear action.<BR>
	 * <BR>
	 */
	@Override
	protected void runImpl()
	{
		// Get the current reader and return if null
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		// If Alternate rule Karma punishment is set to true, forbid Wear to reader with Karma
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			return;
		}
		
		// Check current target of the reader and the INTERACTION_DISTANCE
		L2Object target = player.getTarget();
		if (!player.isGM() && ((target == null // No target (ie GM Shop)
			) || !((target instanceof L2MerchantInstance) || (target instanceof L2MercManagerInstance)) // Target not a merchant and not mercmanager
		|| !player.isInsideRadius(target, L2NpcInstance.INTERACTION_DISTANCE, false, false) // Distance is too far
		))
		{
			return;
		}
		
		MerchantShop list = null;
		
		// Get the current merchant targeted by the reader
		L2MerchantInstance merchant = ((target != null) && (target instanceof L2MerchantInstance)) ? (L2MerchantInstance) target : null;
		
		List<MerchantShop> lists = TradeController.getInstance().getBuyListByNpcId(merchant.getNpcId());
		
		if (lists == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		for (MerchantShop tradeList : lists)
		{
			if (tradeList.getId() == _listId)
			{
				list = tradeList;
			}
		}
		
		if (list == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		_listId = list.getId();
		
		// Check if the quantity of Item to Wear
		if ((_count < 1) || (_listId >= 1000000))
		{
			sendPacket(new ActionFailed());
			return;
		}
		
		// Total Price of the Try On
		long totalPrice = 0;
		
		// Check for buylist validity and calculates summary values
		int slots = 0;
		int weight = 0;
		
		for (int i = 0; i < _count; i++)
		{
			int itemId = _items[i];
			
			if (!list.containsItemId(itemId))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
				return;
			}
			
			ItemTemplate template = ItemTable.getInstance().getTemplate(itemId);
			weight += template.getWeight();
			slots++;
			
			totalPrice += Config.WEAR_PRICE;
			if (totalPrice > Integer.MAX_VALUE)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		// Check the weight
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		// Check the inventory capacity
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan because a Try On is not Free
		if ((totalPrice < 0) || !player.reduceAdena("Wear", (int) totalPrice, player.getLastFolkNPC(), false))
		{
			sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Proceed the wear
		InventoryUpdate playerIU = new InventoryUpdate();
		for (int i = 0; i < _count; i++)
		{
			int itemId = _items[i];
			
			if (!list.containsItemId(itemId))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
				return;
			}
			
			// If reader doesn't own this item : Add this L2ItemInstance to Inventory and set properties lastchanged to ADDED and _wear to True
			// If reader already own this item : Return its L2ItemInstance (will not be destroy because property _wear set to False)
			L2ItemInstance item = player.getInventory().addWearItem("Wear", itemId, player, merchant);
			
			// Equip reader with this item (set its location)
			player.getInventory().equipItemAndRecord(item);
			
			// Add this Item in the InventoryUpdate Server->Client Packet
			playerIU.addItem(item);
		}
		
		// Send the InventoryUpdate Server->Client Packet to the reader
		// Add Items in reader inventory and equip them
		player.sendPacket(playerIU);
		
		// Send the StatusUpdate Server->Client Packet to the reader with new CUR_LOAD (0x0e) information
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		
		// Send a Server->Client packet UserInfo to this L2PcInstance and CharInfo to allTemplates L2PcInstance in its _KnownPlayers
		player.broadcastUserInfo();
		
		// All weared items should be removed in ALLOW_WEAR_DELAY sec.
		if (_removeWearItemsTask == null)
		{
			_removeWearItemsTask = ThreadPoolManager.getInstance().scheduleGeneral(new RemoveWearItemsTask(), Config.WEAR_DELAY * 1000);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__C6_REQUESTWEARITEM;
	}
}
