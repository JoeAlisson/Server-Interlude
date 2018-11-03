package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Clan.SubPledge;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * sample 0000: 68 b1010000 48 00 61 00 6d 00 62 00 75 00 72 00 67 00 00 00 H.a.m.b.u.r.g... 43 00 61 00 6c 00 61 00 64 00 6f 00 6e 00 00 00 C.a.l.a.d.o.n... 00000000 crestid | not used (nuocnam) 00000000 00000000 00000000 00000000 22000000 00000000 00000000 00000000 ally id 00 00 ally name 00000000
 * ally crrest id 02000000 6c 00 69 00 74 00 68 00 69 00 75 00 6d 00 31 00 00 00 l.i.t.h.i.u.m... 0d000000 level 12000000 class id 00000000 01000000 offline 1=true 00000000 45 00 6c 00 61 00 6e 00 61 00 00 00 E.l.a.n.a... 08000000 19000000 01000000 01000000 00000000 format dSS dddddddddSdd d
 * (Sddddd) dddSS dddddddddSdd d (Sdddddd)
 * @version $Revision: 1.6.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgeShowMemberListAll extends L2GameServerPacket
{
	private final L2Clan _clan;
	private final L2PcInstance _activeChar;
	private final L2ClanMember[] _members;
	private int _pledgeType;
	
	public PledgeShowMemberListAll(L2Clan clan, L2PcInstance activeChar)
	{
		_clan = clan;
		_activeChar = activeChar;
		_members = _clan.getMembers();
	}
	
	@Override
	protected final void writeImpl()
	{
		
		_pledgeType = 0;
		writePledge(0);
		
		SubPledge[] subPledge = _clan.getAllSubPledges();
		for (SubPledge element : subPledge)
		{
			_activeChar.sendPacket(new PledgeReceiveSubPledgeCreated(element));
		}
		
		for (L2ClanMember m : _members)
		{
			if (m.getPledgeType() == 0)
			{
				continue;
			}
			_activeChar.sendPacket(new PledgeShowMemberListAdd(m));
		}
		
		// unless this is sent sometimes, the client doesn't recognise the reader as the leader
		_activeChar.sendPacket(new UserInfo(_activeChar));
		
	}
	
	void writePledge(int mainOrSubpledge)
	{
		writeByte(0x53);
		
		writeInt(mainOrSubpledge); // c5 main clan 0 or any subpledge 1?
		writeInt(_clan.getClanId());
		writeInt(_pledgeType); // c5 - possibly pledge type?
		writeString(_clan.getName());
		writeString(_clan.getLeaderName());
		
		writeInt(_clan.getCrestId()); // crest id .. is used again
		writeInt(_clan.getLevel());
		writeInt(_clan.getCastle());
		writeInt(_clan.getHasHideout());
		writeInt(_clan.getRank()); // not confirmed
		writeInt(_clan.getReputationScore()); // was activechar lvl
		writeInt(0); // 0
		writeInt(0); // 0
		
		writeInt(_clan.getAllyId());
		writeString(_clan.getAllyName());
		writeInt(_clan.getAllyCrestId());
		writeInt(_clan.isAtWar());// new c3
		writeInt(_clan.getSubPledgeMembersCount(_pledgeType));
		
		for (L2ClanMember m : _members)
		{
			if (m.getPledgeType() != _pledgeType)
			{
				continue;
			}
			writeString(m.getName());
			writeInt(m.getLevel());
			writeInt(m.getClassId());
			writeInt(0); // no visible effect
			writeInt(m.getObjectId());// writeInt(1);
			writeInt(m.isOnline() ? 1 : 0); // 1=online 0=offline
			writeInt(0); // c5 makes the name yellow. member is in academy and has a sponsor
		}
	}
}
