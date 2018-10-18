package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.TradeList;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class PrivateStoreListBuy extends L2GameServerPacket
{
	private final L2PcInstance _storePlayer;
	private final L2PcInstance _activeChar;
	private final int _playerAdena;
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
		writeInt(_playerAdena);
		
		writeInt(_items.length);
		
		for (TradeList.TradeItem item : _items)
		{
			writeInt(item.getObjectId());
			writeInt(item.getItem().getId());
			writeShort(item.getEnchant());
			writeInt(item.getCount()); // give max possible sell amount
			
			writeInt(item.getItem().getPrice());
			writeShort(0);
			
			writeInt(item.getItem().getBodyPart().getId());
			writeShort(item.getItem().getType2().getId());
			writeInt(item.getPrice());// buyers price
			
			writeInt(item.getCount()); // maximum possible tradecount
		}
	}
}