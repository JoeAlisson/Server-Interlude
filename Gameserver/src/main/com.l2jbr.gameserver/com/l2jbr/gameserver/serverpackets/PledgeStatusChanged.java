package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Clan;

/**
 * sample 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00 ....H........... 0010: 00 00 00 00 00 ..... format ddddd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PledgeStatusChanged extends L2GameServerPacket
{
	private final L2Clan _clan;
	
	public PledgeStatusChanged(L2Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xcd);
		writeInt(_clan.getLeaderId());
		writeInt(_clan.getClanId());
		writeInt(0);
		writeInt(_clan.getLevel());
		writeInt(0);
		writeInt(0);
		writeInt(0);
	}
}
