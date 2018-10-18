package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class PartySmallWindowAdd extends L2GameServerPacket
{
	private final L2PcInstance _member;
	
	public PartySmallWindowAdd(L2PcInstance member)
	{
		_member = member;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x4f);
		L2PcInstance player = getClient().getActiveChar();
		writeInt(player.getObjectId()); // c3
		writeInt(0);// writeInt(0x04); ?? //c3
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
		writeInt(0);// writeInt(0x01); ??
		writeInt(0);
	}
}
