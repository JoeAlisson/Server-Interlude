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

import com.l2jbr.gameserver.model.L2Position;
import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Maktakien
 */
public class MoveToLocationInVehicle extends L2GameServerPacket
{
	private int _charObjId;
	private int _boatId;
	private L2Position _destination;
	private L2Position _origin;

	public MoveToLocationInVehicle(L2Character actor, L2Position destination, L2Position origin)
	{
		if (!(actor instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) actor;
		
		if (player.getBoat() == null)
		{
			return;
		}
		
		_charObjId = player.getObjectId();
		_boatId = player.getBoat().getObjectId();
		_destination = destination;
		_origin = origin;
		/*
		 * _pci.sendMessage("_destination : x " + x +" y " + y + " z " + z); _pci.sendMessage("_boat : x " + _pci.getBoat().getX() +" y " + _pci.getBoat().getY() + " z " + _pci.getBoat().getZ()); _pci.sendMessage("-----------");
		 */
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0x71);
		writeInt(_charObjId);
		writeInt(_boatId);
		writeInt(_destination.x);
		writeInt(_destination.y);
		writeInt(_destination.z);
		writeInt(_origin.x);
		writeInt(_origin.y);
		writeInt(_origin.z);
	}
}
