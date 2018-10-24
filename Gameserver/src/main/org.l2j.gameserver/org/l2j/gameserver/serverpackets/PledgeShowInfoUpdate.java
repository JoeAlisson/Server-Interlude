package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Clan;

public class PledgeShowInfoUpdate extends L2GameServerPacket
{
	private final L2Clan _clan;
	
	public PledgeShowInfoUpdate(L2Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	protected final void writeImpl()
	{
		// ddddddddddSdd
		writeByte(0x88);
		// sending empty data so client will ask allTemplates the info in response ;)
		writeInt(_clan.getClanId());
		writeInt(0);
		writeInt(_clan.getLevel()); // clan level
		writeInt(_clan.getCastle());
		writeInt(_clan.getHasHideout());
		writeInt(0); // displayed in the "tree" view (with the clan skills)
		writeInt(_clan.getReputationScore()); // clan reputation score
		writeInt(0);
		writeInt(0);
		
		writeInt(0); // c5
		writeString("bili"); // c5
		writeInt(0); // c5
		writeInt(0); // c5
	}
}
