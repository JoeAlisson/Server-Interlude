/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.TradeList;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


/**
 * This class ...
 * @version $Revision: 1.7.2.2.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreListBuy extends L2GameServerPacket
{
	// private static final String _S__D1_PRIVATEBUYLISTBUY = "[S] b8 PrivateBuyListBuy";
	private static final String _S__D1_PRIVATESTORELISTBUY = "[S] b8 PrivateStoreListBuy";
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
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__D1_PRIVATESTORELISTBUY;
	}
}
