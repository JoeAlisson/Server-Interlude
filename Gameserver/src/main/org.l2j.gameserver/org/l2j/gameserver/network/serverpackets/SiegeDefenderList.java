package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.datatables.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.entity.Castle;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 * @author KenM
 */
public class SiegeDefenderList extends L2GameServerPacket
{
	private final Castle _castle;
	
	public SiegeDefenderList(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xcb);
		writeInt(_castle.getCastleId());
		writeInt(0x00); // 0
		writeInt(0x01); // 1
		writeInt(0x00); // 0
		int size = _castle.getSiege().getDefenderClans().size() + _castle.getSiege().getDefenderWaitingClans().size();
		if (size > 0)
		{
			L2Clan clan;
			
			writeInt(size);
			writeInt(size);
			// Listing the Lord and the approved clans
			for (L2SiegeClan siegeclan : _castle.getSiege().getDefenderClans())
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
				switch (siegeclan.getType())
				{
					case OWNER:
						writeInt(0x01); // owner
						break;
					case DEFENDER_PENDING:
						writeInt(0x02); // approved
						break;
					case DEFENDER:
						writeInt(0x03); // waiting approved
						break;
					default:
						writeInt(0x00);
						break;
				}
				writeInt(clan.getAllyId());
				writeString(clan.getAllyName());
				writeString(""); // AllyLeaderName
				writeInt(clan.getAllyCrestId());
			}
			for (L2SiegeClan siegeclan : _castle.getSiege().getDefenderWaitingClans())
			{
				clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
				writeInt(clan.getClanId());
				writeString(clan.getName());
				writeString(clan.getLeaderName());
				writeInt(clan.getCrestId());
				writeInt(0x00); // signed time (seconds) (not storated by L2J)
				writeInt(0x02); // waiting approval
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
}
