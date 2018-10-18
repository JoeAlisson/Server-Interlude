package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class PartySmallWindowDelete extends L2GameServerPacket
{
	private final L2PcInstance _member;
	
	public PartySmallWindowDelete(L2PcInstance member)
	{
		_member = member;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x51);
		writeInt(_member.getObjectId());
		writeString(_member.getName());
	}
}
