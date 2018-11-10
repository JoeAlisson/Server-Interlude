package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Summon;

public class PetStatusShow extends L2GameServerPacket
{
	private final int _summonType;
	
	public PetStatusShow(L2Summon summon)
	{
		_summonType = summon.getSummonType();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xB0);
		writeInt(_summonType);
	}
}
