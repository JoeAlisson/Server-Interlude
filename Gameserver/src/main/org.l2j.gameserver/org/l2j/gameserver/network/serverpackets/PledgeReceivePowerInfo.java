package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ClanMember;

/**
 * Format : (ch) dSd
 * @author -Wooden-
 */
public class PledgeReceivePowerInfo extends L2GameServerPacket
{
	private final L2ClanMember _member;

	public PledgeReceivePowerInfo(L2ClanMember member)
	{
		_member = member;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x3c);
		
		writeInt(_member.getPowerGrade()); // power grade
		writeString(_member.getName());
		writeInt(_member.getClan().getRankPrivs(_member.getPowerGrade())); // privileges
	}
}
