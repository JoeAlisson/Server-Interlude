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

import com.l2jbr.gameserver.datatables.ClanTable;
import com.l2jbr.gameserver.model.L2Clan;
import com.l2jbr.gameserver.model.L2SiegeClan;
import com.l2jbr.gameserver.model.entity.Castle;


//import java.util.Calendar; //signed time related
//

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xca<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 * @author KenM
 */
public class SiegeAttackerList extends L2GameServerPacket
{
	private static final String _S__CA_SiegeAttackerList = "[S] ca SiegeAttackerList";
	// private static Logger _log = LoggerFactory.getLogger(SiegeAttackerList.class.getName());
	private final Castle _castle;
	
	public SiegeAttackerList(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xca);
		writeInt(_castle.getCastleId());
		writeInt(0x00); // 0
		writeInt(0x01); // 1
		writeInt(0x00); // 0
		int size = _castle.getSiege().getAttackerClans().size();
		if (size > 0)
		{
			L2Clan clan;
			
			writeInt(size);
			writeInt(size);
			for (L2SiegeClan siegeclan : _castle.getSiege().getAttackerClans())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				if (clan == null)
				{
					continue;
				}
				
				writeInt(clan.getClanId());
				writeString(clan.getName());
				writeString(clan.getLeaderName());
				writeInt(clan.getCrestId());
				writeInt(0x00); // signed time (seconds) (not storated by L2J)
				writeInt(clan.getAllyId());
				writeString(clan.getAllyName());
				writeString(""); // AllyLeaderName
				writeInt(clan.getAllyCrestId());
			}
		}
		else
		{
			writeInt(0x00);
			writeInt(0x00);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__CA_SiegeAttackerList;
	}
	
}
