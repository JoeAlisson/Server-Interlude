package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.L2MerchantInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class PrivateStoreListSell extends L2GameServerPacket
{
	private L2PcInstance _storePlayer;
	private final L2PcInstance _activeChar;
	private final int _playerAdena;
	private final boolean _packageSale;
	private final TradeList.TradeItem[] _items;
	
	public PrivateStoreListSell(L2PcInstance player, L2PcInstance storePlayer)
	{
		_activeChar = player;
		_storePlayer = storePlayer;
		_playerAdena = _activeChar.getAdena();
		_items = _storePlayer.getSellList().getItems();
		_packageSale = _storePlayer.getSellList().isPackaged();
	}
	
	// lease shop
	@Deprecated
	public PrivateStoreListSell(L2PcInstance player, L2MerchantInstance storeMerchant)
	{
		_activeChar = player;
		_playerAdena = _activeChar.getAdena();
		_items = _storePlayer.getSellList().getItems();
		_packageSale = _storePlayer.getSellList().isPackaged();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x9b);
		writeInt(_storePlayer.getObjectId());
		writeInt(_packageSale ? 1 : 0);
		writeInt(_playerAdena);
		
		writeInt(_items.length);
		for (TradeList.TradeItem item : _items)
		{
			writeInt(item.getItem().getType2().getId());
			writeInt(item.getObjectId());
			writeInt(item.getItem().getId());
			writeInt(item.getCount());
			writeShort(0x00);
			writeShort(item.getEnchant());
			writeShort(0x00);
			writeInt(item.getItem().getBodyPart().getId());
			writeInt(item.getPrice()); // your price
			writeInt(item.getItem().getPrice()); // store price
		}
	}
}