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

import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


/**
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class GMViewItemList extends L2GameServerPacket
{
	// private static Logger _log = LoggerFactory.getLogger(GMViewItemList.class.getName());
	private static final String _S__AD_GMVIEWITEMLIST = "[S] 94 GMViewItemList";
	private final L2ItemInstance[] _items;
	private final L2PcInstance _cha;
	private final String _playerName;
	
	public GMViewItemList(L2PcInstance cha)
	{
		_items = cha.getInventory().getItems();
		_playerName = cha.getName();
		_cha = cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x94);
		writeString(_playerName);
		writeInt(_cha.GetInventoryLimit()); // inventory limit
		writeShort(0x01); // show window ??
		writeShort(_items.length);
		
		for (L2ItemInstance temp : _items)
		{
			if ((temp == null) || (temp.getItem() == null))
			{
				continue;
			}
			
			writeShort(temp.getItem().getType1().getId());
			
			writeInt(temp.getObjectId());
			writeInt(temp.getItemId());
			writeInt(temp.getCount());
			writeShort(temp.getItem().getType2().getId());
			writeShort(temp.getCustomType1());
			writeShort(temp.isEquipped() ? 0x01 : 0x00);
			writeInt(temp.getItem().getBodyPart().getId());
			writeShort(temp.getEnchantLevel());
			writeShort(temp.getCustomType2());
			if (temp.isAugmented())
			{
				writeInt(temp.getAugmentation().getAugmentationId());
			}
			else
			{
				writeInt(0x00);
			}
			writeInt(-1); // C6
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__AD_GMVIEWITEMLIST;
	}
}
