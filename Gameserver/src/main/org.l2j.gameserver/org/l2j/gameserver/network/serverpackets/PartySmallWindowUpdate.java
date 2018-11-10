package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class PartySmallWindowUpdate extends L2GameServerPacket
{
	private final L2PcInstance _member;
	
	public PartySmallWindowUpdate(L2PcInstance member)
	{
		_member = member;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x52);
		writeInt(_member.getObjectId());
		writeString(_member.getName());
		
		writeInt((int) _member.getCurrentCp()); // c4
		writeInt(_member.getMaxCp()); // c4
		
		writeInt((int) _member.getCurrentHp());
		writeInt(_member.getMaxHp());
		writeInt((int) _member.getCurrentMp());
		writeInt(_member.getMaxMp());
		writeInt(_member.getLevel());
		writeInt(_member.getPlayerClass().getId());
	}
}
