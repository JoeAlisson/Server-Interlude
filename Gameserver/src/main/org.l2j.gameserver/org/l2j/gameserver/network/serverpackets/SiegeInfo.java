package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.datatables.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = CastleID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related?
 * @author KenM
 */
public class SiegeInfo extends L2GameServerPacket
{
	private static Logger _log = LoggerFactory.getLogger(SiegeInfo.class.getName());
	private final Castle _castle;
	
	public SiegeInfo(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	protected final void writeImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		writeByte(0xc9);
		writeInt(_castle.getCastleId());
		writeInt(((_castle.getOwnerId() == activeChar.getClanId()) && (activeChar.isClanLeader())) ? 0x01 : 0x00);
		writeInt(_castle.getOwnerId());
		if (_castle.getOwnerId() > 0)
		{
			L2Clan owner = ClanTable.getInstance().getClan(_castle.getOwnerId());
			if (owner != null)
			{
				writeString(owner.getName()); // Clan Name
				writeString(owner.getLeaderName()); // Clan Leader Name
				writeInt(owner.getAllyId()); // Ally ID
				writeString(owner.getAllyName()); // Ally Name
			}
			else
			{
				_log.warn("Null owner for castle: " + _castle.getName());
			}
		}
		else
		{
			writeString("NPC"); // Clan Name
			writeString(""); // Clan Leader Name
			writeInt(0); // Ally ID
			writeString(""); // Ally Name
		}
		
		writeInt((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeInt((int) (_castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
		writeInt(0x00); // number of choices?
	}
}
