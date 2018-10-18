package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Clan;
import com.l2jbr.gameserver.model.L2ClanMember;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class GMViewPledgeInfo extends L2GameServerPacket
{
	private final L2Clan _clan;
	private final L2PcInstance _activeChar;
	
	public GMViewPledgeInfo(L2Clan clan, L2PcInstance activeChar)
	{
		_clan = clan;
		_activeChar = activeChar;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x90);
		writeString(_activeChar.getName());
		writeInt(_clan.getClanId());
		writeInt(0x00);
		writeString(_clan.getName());
		writeString(_clan.getLeaderName());
		writeInt(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
		writeInt(_clan.getLevel());
		writeInt(_clan.getCastle());
		writeInt(_clan.getHasHideout());
		writeInt(_clan.getRank());
		writeInt(_clan.getReputationScore());
		writeInt(0);
		writeInt(0);
		
		writeInt(_clan.getAllyId()); // c2
		writeString(_clan.getAllyName()); // c2
		writeInt(_clan.getAllyCrestId()); // c2
		writeInt(_clan.isAtWar()); // c3
		
		L2ClanMember[] members = _clan.getMembers();
		writeInt(members.length);
		
		for (L2ClanMember member : members)
		{
			writeString(member.getName());
			writeInt(member.getLevel());
			writeInt(member.getClassId());
			writeInt(0);
			writeInt(1);
			writeInt(member.isOnline() ? member.getObjectId() : 0);
			writeInt(0);
		}
	}
}
