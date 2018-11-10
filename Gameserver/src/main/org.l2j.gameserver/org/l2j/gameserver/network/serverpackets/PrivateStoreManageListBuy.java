package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class PrivateStoreManageListBuy extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final long _playerAdena;
	private final L2ItemInstance[] _itemList;
	private final TradeList.TradeItem[] _buyList;
	
	public PrivateStoreManageListBuy(L2PcInstance player)
	{
		_activeChar = player;
		_playerAdena = _activeChar.getAdena();
		_itemList = _activeChar.getInventory().getUniqueItems(false, true);
		_buyList = _activeChar.getBuyList().getItems();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xb7);
		// section 1
		writeInt(_activeChar.getObjectId());
		writeLong(_playerAdena);
		
		// section2
		writeInt(_itemList.length); // inventory items for potential buy
		for (L2ItemInstance item : _itemList)
		{
			writeInt(item.getId());
			writeShort(0); // show enchant lvl as 0, as you can't buy enchanted weapons
			writeLong(item.getCount());
			writeLong(item.getReferencePrice());
			writeShort(0x00);
			writeInt(0);// TODO item.getItem().getBodyPart().getId());
			writeShort(item.getCommissionType().ordinal());
		}
		
		// section 3
		writeInt(_buyList.length); // count for allTemplates items already added for buy
		for (TradeList.TradeItem item : _buyList)
		{
			writeInt(item.getItem().getId());
			writeShort(0);
			writeLong(item.getCount());
			writeLong(item.getItem().getPrice());
			writeShort(0x00);
			writeInt(0); // TODO item.getItem().getBodyPart().getId());
			writeShort(item.getItem().getCommissionType().ordinal());
			writeLong(item.getPrice());// your price
			writeLong(item.getItem().getPrice());// fixed store price
		}
	}
}
