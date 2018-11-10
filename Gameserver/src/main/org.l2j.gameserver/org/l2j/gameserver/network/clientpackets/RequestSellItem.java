package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.Util;

public final class RequestSellItem extends L2GameClientPacket
{
	private static final String _C__1E_REQUESTSELLITEM = "[C] 1E RequestSellItem";
	
	private int _listId;
	private int _count;
	private int[] _items; // count*3
	
	/**
	 * packet type id 0x1e sample 1e 00 00 00 00 // list id 02 00 00 00 // number of items 71 72 00 10 // object id ea 05 00 00 // item id 01 00 00 00 // item count 76 4b 00 10 // object id 2e 0a 00 00 // item id 01 00 00 00 // item count format: cdd (ddd)
	 */
	@Override
	protected void readImpl()
	{
		_listId = readInt();
		_count = readInt();
		if ((_count <= 0) || ((_count * 12) > availableData()) || (_count > Config.MAX_ITEM_IN_PACKET))
		{
			_count = 0;
			_items = null;
			return;
		}
		_items = new int[_count * 3];
		for (int i = 0; i < _count; i++)
		{
			int objectId = readInt();
			_items[(i * 3) + 0] = objectId;
			int itemId = readInt();
			_items[(i * 3) + 1] = itemId;
			long cnt = readInt();
			if ((cnt > Integer.MAX_VALUE) || (cnt <= 0))
			{
				_count = 0;
				_items = null;
				return;
			}
			_items[(i * 3) + 2] = (int) cnt;
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
			) || !((target instanceof L2MerchantInstance) || (target instanceof L2MercManagerInstance)) // Target not a merchant and not mercmanager
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
		
		if (_listId > 1000000) // lease
		{
			if (merchant.getNpcId() != (_listId - 1000000))
			{
				sendPacket(new ActionFailed());
				return;
			}
		}
		
		long totalPrice = 0;
		// Proceed the sell
		for (int i = 0; i < _count; i++)
		{
			int objectId = _items[(i * 3) + 0];
			int count = _items[(i * 3) + 2];
			
			if ((count < 0) || (count > Integer.MAX_VALUE))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.", Config.DEFAULT_PUNISH);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				sm = null;
				return;
			}
			
			L2ItemInstance item = player.checkItemManipulation(objectId, count, "sell");
			if ((item == null) || (!item.isSellable()))
			{
				continue;
			}
			
			totalPrice += (item.getReferencePrice() * count) / 2;
			if (totalPrice > Integer.MAX_VALUE)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
			
			item = player.getInventory().destroyItem("Sell", objectId, count, player, null);
			
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) int price = item.getReferencePrice()*(int)count/2; L2ItemInstance li = null; L2ItemInstance la = null; if (_listId > 1000000) { li = merchant.findLeaseItem(item.getId(),item.getEnchantLevel()); la = merchant.getLeaseAdena(); if
			 * (li == null || la == null) continue; price = li.getPriceToBuy()*(int)count; // reader sells, thus merchant buys. if (price > la.getCount()) continue; }
			 */
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) if (item != null && _listId > 1000000) { li.setCount(li.getCount()+(int)count); li.updateDatabase(); la.setCount(la.getCount()-price); la.updateDatabase(); }
			 */
		}
		player.addAdena("Sell", (int) totalPrice, merchant, false);
		
		String html = HtmCache.getInstance().getHtm("data/html/" + htmlFolder + "/" + merchant.getNpcId() + "-sold.htm");
		
		if (html != null)
		{
			NpcHtmlMessage soldMsg = new NpcHtmlMessage(merchant.getObjectId());
			soldMsg.setHtml(html.replaceAll("%objectId%", String.valueOf(merchant.getObjectId())));
			player.sendPacket(soldMsg);
		}
		
		// Update current load as well
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		player.sendPacket(new ItemList(player, true));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__1E_REQUESTSELLITEM;
	}
}