package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * 3 section to this packet 1)playerinfo which is always sent dd 2)list of items which can be added to sell d(hhddddhhhd) 3)list of items which have already been setup for sell in previous sell private store sell manageent d(hhddddhhhdd) *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreManageListSell extends L2GameServerPacket
{
	private static final String _S__B3_PRIVATESELLLISTSELL = "[S] 9a PrivateSellListSell";
	private final L2PcInstance _activeChar;
	private final int _playerAdena;
	private final boolean _packageSale;
	private final TradeList.TradeItem[] _itemList;
	private final TradeList.TradeItem[] _sellList;
	
	public PrivateStoreManageListSell(L2PcInstance player)
	{
		_activeChar = player;
		_playerAdena = _activeChar.getAdena();
		_activeChar.getSellList().updateItems();
		_packageSale = _activeChar.getSellList().isPackaged();
		_itemList = _activeChar.getInventory().getAvailableItems(_activeChar.getSellList());
		_sellList = _activeChar.getSellList().getItems();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x9a);
		// section 1
		writeInt(_activeChar.getObjectId());
		writeInt(_packageSale ? 1 : 0); // Package sell
		writeInt(_playerAdena);
		
		// section2
		writeInt(_itemList.length); // for potential sells
		for (TradeList.TradeItem item : _itemList)
		{
			writeInt(item.getItem().getType2().getId());
			writeInt(item.getObjectId());
			writeInt(item.getItem().getId());
			writeInt(item.getCount());
			writeShort(0);
			writeShort(item.getEnchant());// enchant lvl
			writeShort(0);
			writeInt(item.getItem().getBodyPart().getId());
			writeInt(item.getPrice()); // store price
		}
		// section 3
		writeInt(_sellList.length); // count for any items already added for sell
		for (TradeList.TradeItem item : _sellList)
		{
			writeInt(item.getItem().getType2().getId());
			writeInt(item.getObjectId());
			writeInt(item.getItem().getId());
			writeInt(item.getCount());
			writeShort(0);
			writeShort(item.getEnchant());// enchant lvl
			writeShort(0x00);
			writeInt(item.getItem().getBodyPart().getId());
			writeInt(item.getPrice());// your price
			writeInt(item.getItem().getPrice()); // store price
		}
	}
}
