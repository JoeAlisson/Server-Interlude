package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.TradeController;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.model.entity.database.MerchantShop;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.*;
import org.l2j.gameserver.templates.xml.jaxb.Item;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class RequestBuyItem extends L2GameClientPacket
{
	private static final String _C__1F_REQUESTBUYITEM = "[C] 1F RequestBuyItem";
	private static Logger _log = LoggerFactory.getLogger(RequestBuyItem.class.getName());
	
	private int _listId;
	private int _count;
	private int[] _items; // count*2
	
	@Override
	protected void readImpl()
	{
		_listId = readInt();
		_count = readInt();
		// count*8 is the size of a for iteration of each item
		if (((_count * 2) < 0) || ((_count * 8) > availableData()) || (_count > Config.MAX_ITEM_IN_PACKET))
		{
			_count = 0;
		}
		
		_items = new int[_count * 2];
		for (int i = 0; i < _count; i++)
		{
			int itemId = readInt();
			_items[(i * 2) + 0] = itemId;
			long cnt = readInt();
			if ((cnt > Integer.MAX_VALUE) || (cnt < 0))
			{
				_count = 0;
				_items = null;
				return;
			}
			_items[(i * 2) + 1] = (int) cnt;
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
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			return;
		}
		
		L2Object target = player.getTarget();
		if (!player.isGM() && ((target == null // No target (ie GM Shop)
			) || !((target instanceof L2MerchantInstance) || (target instanceof L2FishermanInstance) || (target instanceof L2MercManagerInstance) || (target instanceof L2ClanHallManagerInstance) || (target instanceof L2CastleChamberlainInstance)) // Target not a merchant, fisherman or mercmanager
		|| !player.isInsideRadius(target, L2NpcInstance.INTERACTION_DISTANCE, false, false) // Distance is too far
		))
		{
			return;
		}
		
		boolean ok = true;
		String htmlFolder = "";
		
		if (target != null)
		{
			if (target instanceof L2MerchantInstance)
			{
				htmlFolder = "merchant";
			}
			else if (target instanceof L2FishermanInstance)
			{
				htmlFolder = "fisherman";
			}
			else if (target instanceof L2MercManagerInstance)
			{
				ok = true;
			}
			else if (target instanceof L2ClanHallManagerInstance)
			{
				ok = true;
			}
			else if (target instanceof L2CastleChamberlainInstance)
			{
				ok = true;
			}
			else
			{
				ok = false;
			}
		}
		else
		{
			ok = false;
		}
		
		L2NpcInstance merchant = null;
		
		if (ok)
		{
			merchant = (L2NpcInstance) target;
		}
		else if (!ok && !player.isGM())
		{
			player.sendMessage("Invalid Target: Seller must be targetted");
			return;
		}
		
		MerchantShop list = null;
		
		if (merchant != null)
		{
			List<MerchantShop> lists = TradeController.getInstance().getBuyListByNpcId(merchant.getNpcId());
			
			if (!player.isGM())
			{
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
			}
			else
			{
				list = TradeController.getInstance().getBuyList(_listId);
			}
		}
		else
		{
			list = TradeController.getInstance().getBuyList(_listId);
		}
		if (list == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		_listId = list.getId();
		
		if (_listId > 1000000) // lease
		{
			if ((merchant != null) && (merchant.getTemplate().getId() != (_listId - 1000000)))
			{
				sendPacket(new ActionFailed());
				return;
			}
		}
		if (_count < 1)
		{
			sendPacket(new ActionFailed());
			return;
		}
		double taxRate = 0;
		if ((merchant != null) && merchant.getIsInTown())
		{
			taxRate = merchant.getCastle().getTaxRate();
		}
		long subTotal = 0;
		int tax = 0;
		
		// Check for buylist validity and calculates summary values
		long slots = 0;
		long weight = 0;
		for (int i = 0; i < _count; i++)
		{
			int itemId = _items[(i * 2) + 0];
			int count = _items[(i * 2) + 1];
			int price = -1;
			
			if (!list.containsItemId(itemId))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
				return;
			}
			
			ItemTemplate template = ItemTable.getInstance().getTemplate(itemId);
			
			if (template == null)
			{
				continue;
			}
			if ((count > Integer.MAX_VALUE) || ((!(template instanceof Item)  || !((Item)template).isStackable()) && (count > 1)))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase invalid quantity of items at the same time.", Config.DEFAULT_PUNISH);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				sm = null;
				
				return;
			}
			
			if (_listId < 1000000)
			{
				// list = TradeController.getInstance().getBuyList(_listId);
				price = list.getPriceForItemId(itemId);
				if ((itemId >= 3960) && (itemId <= 4026))
				{
					price *= Config.RATE_SIEGE_GUARDS_PRICE;
				}
				
			}
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) } else { L2ItemInstance li = merchant.findLeaseItem(itemId, 0); if (li == null || li.getCount() < cnt) { cnt = li.getCount(); if (cnt <= 0) { items.remove(i); continue; } items.get(i).setCount((int)cnt); } price =
			 * li.getPriceToSell(); // lease holder sells the item weight = li.getItem().getWeight(); }
			 */
			if (price < 0)
			{
				_log.warn("ERROR, no price found .. wrong buylist ??");
				sendPacket(new ActionFailed());
				return;
			}
			
			if ((price == 0) && !player.isGM() && Config.ONLY_GM_ITEMS_FREE)
			{
				player.sendMessage("Ohh Cheat dont work? You have a problem now!");
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried buy item for 0 adena.", Config.DEFAULT_PUNISH);
				return;
			}
			
			subTotal += (long) count * price; // Before tax
			tax = (int) (subTotal * taxRate);
			if ((subTotal + tax) > Integer.MAX_VALUE)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
			
			weight += (long) count * template.getWeight();
			if (!(template instanceof  Item) || !((Item)template).isStackable())
			{
				slots += count;
			}
			else if (player.getInventory().getItemByItemId(itemId) == null)
			{
				slots++;
			}
		}
		
		if ((weight > Integer.MAX_VALUE) || (weight < 0) || !player.getInventory().validateWeight((int) weight))
		{
			sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if ((slots > Integer.MAX_VALUE) || (slots < 0) || !player.getInventory().validateCapacity((int) slots))
		{
			sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan
		if ((subTotal < 0) || !player.reduceAdena("Buy", (int) (subTotal + tax), player.getLastFolkNPC(), false))
		{
			sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		if ((merchant != null) && merchant.getIsInTown() && (merchant.getCastle().getOwnerId() > 0))
		{
			merchant.getCastle().addToTreasury(tax);
		}
		
		// Proceed the purchase
		for (int i = 0; i < _count; i++)
		{
			int itemId = _items[(i * 2)];
			int count = _items[(i * 2) + 1];
			if (count < 0)
			{
				count = 0;
			}
			
			if (!list.containsItemId(itemId))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.", Config.DEFAULT_PUNISH);
				return;
			}
			if (list.isLimited(itemId))
			{
				list.decreaseCount(itemId, count);
			}
			// Add item to Inventory and adjust update packet
			player.getInventory().addItem("Buy", itemId, count, player, merchant);
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) // Update Leaseholder list if (_listId >= 1000000) { L2ItemInstance li = merchant.findLeaseItem(item.getId(), 0); if (li == null) continue; if (li.getCount() < item.getCount()) item.setCount(li.getCount());
			 * li.setCount(li.getCount() - item.getCount()); li.updateDatabase(); price = item.getCount() + li.getPriceToSell(); L2ItemInstance la = merchant.getLeaseAdena(); la.setCount(la.getCount() + price); la.updateDatabase(); reader.getInventory().addItem(item); item.updateDatabase(); }
			 */
		}
		
		if (merchant != null)
		{
			String html = HtmCache.getInstance().getHtm("data/html/" + htmlFolder + "/" + merchant.getNpcId() + "-bought.htm");
			
			if (html != null)
			{
				NpcHtmlMessage boughtMsg = new NpcHtmlMessage(merchant.getObjectId());
				boughtMsg.setHtml(html.replaceAll("%objectId%", String.valueOf(merchant.getObjectId())));
				player.sendPacket(boughtMsg);
			}
		}
		
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		player.sendPacket(new ItemList(player, true));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__1F_REQUESTBUYITEM;
	}
}
