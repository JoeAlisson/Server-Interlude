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
package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: (ch)ddddddd d: Number of Inventory Slots d: Number of Warehouse Slots d: Number of Freight Slots (unconfirmed) (200 for a low level DWARF) d: Private Sell Store Slots (unconfirmed) (4 for a low level DWARF) d: Private Buy Store Slots (unconfirmed) (5 for a low level DWARF) d: Dwarven
 * Recipe Book Slots d: Normal Recipe Book Slots
 * @author -Wooden- format from KenM
 */
public class ExStorageMaxCount extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final int _inventory;
	private final int _warehouse;
	private final int _freight;
	private final int _privateSell;
	private final int _privateBuy;
	private final int _receipeD;
	private final int _recipe;
	
	public ExStorageMaxCount(L2PcInstance character)
	{
		_activeChar = character;
		_inventory = _activeChar.getInventoryLimit();
		_warehouse = _activeChar.GetWareHouseLimit();
		_privateSell = _activeChar.GetPrivateSellStoreLimit();
		_privateBuy = _activeChar.GetPrivateBuyStoreLimit();
		_freight = _activeChar.GetFreightLimit();
		_receipeD = _activeChar.GetDwarfRecipeLimit();
		_recipe = _activeChar.GetCommonRecipeLimit();
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x2e);
		
		writeInt(_inventory);
		writeInt(_warehouse);
		writeInt(_freight);
		writeInt(_privateSell);
		writeInt(_privateBuy);
		writeInt(_receipeD);
		writeInt(_recipe);
	}
}