package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Summon;
import com.l2jbr.gameserver.model.actor.instance.L2PetInstance;

public class PetStatusUpdate extends L2GameServerPacket
{
	private final L2Summon _summon;
	private final int _maxHp, _maxMp;
	private int _maxFed, _curFed;
	
	public PetStatusUpdate(L2Summon summon)
	{
		_summon = summon;
		_maxHp = _summon.getMaxHp();
		_maxMp = _summon.getMaxMp();
		if (_summon instanceof L2PetInstance)
		{
			L2PetInstance pet = (L2PetInstance) _summon;
			_curFed = pet.getCurrentFed(); // how fed it is
			_maxFed = pet.getMaxFed(); // max fed it can be
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xb5);
		writeInt(_summon.getSummonType());
		writeInt(_summon.getObjectId());
		writeInt(_summon.getX());
		writeInt(_summon.getY());
		writeInt(_summon.getZ());
		writeString(_summon.getTitle());
		writeInt(_curFed);
		writeInt(_maxFed);
		writeInt((int) _summon.getCurrentHp());
		writeInt(_maxHp);
		writeInt((int) _summon.getCurrentMp());
		writeInt(_maxMp);
		writeInt(_summon.getLevel());
		writeLong(_summon.getStat().getExp());
		writeLong(_summon.getExpForThisLevel());// 0% absolute value
		writeLong(_summon.getExpForNextLevel());// 100% absolute value
	}
}
