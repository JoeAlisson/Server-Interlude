package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class PrivateStoreListBuy extends L2GameServerPacket
{
	private final L2PcInstance _storePlayer;
	private final L2PcInstance _activeChar;
	private final long _playerAdena;
	private final TradeList.TradeItem[] _items;
	
	public PrivateStoreListBuy(L2PcInstance player, L2PcInstance storePlayer)
	{
		_storePlayer = storePlayer;
		_activeChar = player;
		_playerAdena = _activeChar.getAdena();
		_storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
		_items = _storePlayer.getBuyList().getAvailableItems(_activeChar.getInventory());
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xb8);
		writeInt(_storePlayer.getObjectId());
		writeLong(_playerAdena);
		
		writeInt(_items.length);
		
		for (TradeList.TradeItem item : _items)
		{
			writeInt(item.getObjectId());
			writeInt(item.getItem().getId());
			writeShort(item.getEnchant());
			writeLong(item.getCount()); // give max possible sell amount
			
			writeLong(item.getItem().getPrice());
			writeShort(0);
			
			writeInt(0);// TODO item.getItem().getBodyPart().getId());
			writeShort(item.getItem().getCommissionType().ordinal());
			writeLong(item.getPrice());// buyers price
			
			writeLong(item.getCount()); // maximum possible tradecount
		}
	}
}