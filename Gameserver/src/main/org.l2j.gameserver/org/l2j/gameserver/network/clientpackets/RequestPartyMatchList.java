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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.PartyMatchList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Packetformat Rev650 cdddddS
 * @version $Revision: 1.1.4.4 $ $Date: 2005/03/27 15:29:30 $
 */

public class RequestPartyMatchList extends L2GameClientPacket
{
	private static final String _C__70_REQUESTPARTYMATCHLIST = "[C] 70 RequestPartyMatchList";
	private static Logger _log = LoggerFactory.getLogger(RequestPartyMatchList.class.getName());
	
	private int _status;
	
	@Override
	protected void readImpl()
	{
		_status = readInt();
		// TODO analyse values _unk1-unk5
		/*
		 * _unk1 = readInt(); _unk2 = readInt(); _unk3 = readInt(); _unk4 = readInt(); _unk5 = readString();
		 */
	}
	
	@Override
	protected void runImpl()
	{
		if (_status == 1)
		{
			// window is open fill the list
			// actually the client should get automatic updates for the list
			// for now we only fill it once
			
			// Collection<L2PcInstance> players = L2World.getInstance().getAllPlayers();
			// L2PcInstance[] allPlayers = players.toArray(new L2PcInstance[players.size()]);
			L2PcInstance[] empty = new L2PcInstance[] {};
			new PartyMatchList(empty);
		}
		else if (_status == 3)
		{
			// client does not need any more updates
			if (Config.DEBUG)
			{
				_log.debug("PartyMatch window was closed.");
			}
		}
		else
		{
			if (Config.DEBUG)
			{
				_log.debug("party match status: " + _status);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__70_REQUESTPARTYMATCHLIST;
	}
}
