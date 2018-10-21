package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Clan.RankPrivs;

/**
 * sample 0000: 9c c10c0000 48 00 61 00 6d 00 62 00 75 00 72 .....H.a.m.b.u.r 0010: 00 67 00 00 00 00000000 00000000 00000000 00000000 00000000 00000000 00 00 00000000 ... format dd ??
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgePowerGradeList extends L2GameServerPacket
{
	private static final String _S__FE_3B_PLEDGEPOWERGRADELIST = "[S] FE:3B PledgePowerGradeList";
	private final RankPrivs[] _privs;
	
	public PledgePowerGradeList(RankPrivs[] privs)
	{
		_privs = privs;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x3b);
		writeInt(_privs.length);
		for (RankPrivs _priv : _privs)
		{
			writeInt(_priv.getRank());
			writeInt(_priv.getParty());
			// _log.warn("rank: "+_privs[i].getRank()+" party: "+_privs[i].getParty());
		}
	}
}
