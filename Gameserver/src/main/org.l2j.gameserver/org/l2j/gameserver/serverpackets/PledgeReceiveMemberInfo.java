package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ClanMember;

/**
 * @author -Wooden-
 */
public class PledgeReceiveMemberInfo extends L2GameServerPacket
{
	private final L2ClanMember _member;

	public PledgeReceiveMemberInfo(L2ClanMember member)
	{
		_member = member;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x3d);
		
		writeInt(_member.getPledgeType());
		writeString(_member.getName());
		writeString(_member.getTitle()); // title
		writeInt(_member.getPowerGrade()); // power
		
		// clan or subpledge name
		if (_member.getPledgeType() != 0)
		{
			writeString((_member.getClan().getSubPledge(_member.getPledgeType())).getName());
		}
		else
		{
			writeString(_member.getClan().getName());
		}
		
		writeString(_member.getApprenticeOrSponsorName()); // name of this member's apprentice/sponsor
	}
}
