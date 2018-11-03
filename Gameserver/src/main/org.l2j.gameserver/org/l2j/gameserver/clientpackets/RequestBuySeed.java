package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2ManorManagerInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.database.SeedProduction;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.InventoryUpdate;
import org.l2j.gameserver.serverpackets.StatusUpdate;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.templates.xml.jaxb.Item;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.util.Util;

/**
 * Format: cdd[dd] c // id (0xC4) d // manor id d // seeds to buy [ d // seed id d // count ]
 * @author l3x
 */
public class RequestBuySeed extends L2GameClientPacket
{
	private static final String _C__C4_REQUESTBUYSEED = "[C] C4 RequestBuySeed";
	
	private int _count;
	private int _manorId;
	private int[] _items; // size _count * 2
	
	@Override
	protected void readImpl()
	{
		_manorId = readInt();
		_count = readInt();
		
		if ((_count > 500) || ((_count * 8) < availableData())) // check values
		{
			_count = 0;
			return;
		}
		
		_items = new int[_count * 2];
		
		for (int i = 0; i < _count; i++)
		{
			int itemId = readInt();
			_items[(i * 2) + 0] = itemId;
			long cnt = readInt();
			if ((cnt > Integer.MAX_VALUE) || (cnt < 1))
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
		long totalPrice = 0;
		int slots = 0;
		int totalWeight = 0;
		
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		if (_count < 1)
		{
			sendPacket(new ActionFailed());
			return;
		}
		
		L2Object target = player.getTarget();
		
		if (!(target instanceof L2ManorManagerInstance))
		{
			target = player.getLastFolkNPC();
		}
		
		if (!(target instanceof L2ManorManagerInstance))
		{
			return;
		}
		
		Castle castle = CastleManager.getInstance().getCastleById(_manorId);
		
		for (int i = 0; i < _count; i++)
		{
			int seedId = _items[(i * 2) + 0];
			int count = _items[(i * 2) + 1];
			int price = 0;
			int residual = 0;
			
			SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			price = seed.getPrice();
			residual = seed.getAmount();
			
			if (price <= 0)
			{
				return;
			}
			
			if (residual < count)
			{
				return;
			}
			
			totalPrice += count * price;
			
			ItemTemplate template = ItemTable.getInstance().getTemplate(seedId);
			totalWeight += count * template.getWeight();
			if (!(template instanceof Item)  ||  !((Item)template).isStackable())
			{
				slots += count;
			}
			else if (player.getInventory().getItemByItemId(seedId) == null)
			{
				slots++;
			}
		}
		
		if (totalPrice > Integer.MAX_VALUE)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!player.getInventory().validateWeight(totalWeight))
		{
			sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		// Charge buyer
		if ((totalPrice < 0) || !player.reduceAdena("Buy", (int) totalPrice, target, false))
		{
			sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Adding to treasury for Manor Castle
		castle.addToTreasuryNoTax((int) totalPrice);
		
		// Proceed the purchase
		InventoryUpdate playerIU = new InventoryUpdate();
		for (int i = 0; i < _count; i++)
		{
			int seedId = _items[(i * 2) + 0];
			int count = _items[(i * 2) + 1];
			if (count < 0)
			{
				count = 0;
			}
			
			// Update Castle Seeds Amount
			SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			seed.setAmount(seed.getAmount() - count);
			if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
			{
				CastleManager.getInstance().getCastleById(_manorId).updateSeed(seed.getSeedId(), seed.getAmount(), CastleManorManager.PERIOD_CURRENT);
			}
			
			// Add item to Inventory and adjust update packet
			L2ItemInstance item = player.getInventory().addItem("Buy", seedId, count, player, target);
			
			if (item.getCount() > count)
			{
				playerIU.addModifiedItem(item);
			}
			else
			{
				playerIU.addNewItem(item);
			}
			
			// Send Char Buy Messages
			SystemMessage sm = null;
			sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
			sm.addItemName(seedId);
			sm.addNumber(count);
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
		return _C__C4_REQUESTBUYSEED;
	}
}
