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

import com.l2jbr.gameserver.model.actor.instance.L2DoorInstance;


/**
 * 61 d6 6d c0 4b door id 8f 14 00 00 x b7 f1 00 00 y 60 f2 ff ff z 00 00 00 00 ?? format dddd rev 377 ID:%d X:%d Y:%d Z:%d ddddd rev 419
 * @version $Revision: 1.3.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class DoorStatusUpdate extends L2GameServerPacket
{
	private static final String _S__61_DOORSTATUSUPDATE = "[S] 4d DoorStatusUpdate";
	private final L2DoorInstance _door;
	
	public DoorStatusUpdate(L2DoorInstance door)
	{
		_door = door;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x4d);
		writeInt(_door.getObjectId());
		writeInt(_door.getOpen());
		writeInt(_door.getDamage());
		writeInt(_door.isEnemyOf(getClient().getActiveChar()) ? 1 : 0);
		writeInt(_door.getDoorId());
		writeInt(_door.getMaxHp());
		writeInt((int) _door.getCurrentHp());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__61_DOORSTATUSUPDATE;
	}
	
}
