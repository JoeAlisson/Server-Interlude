package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;

public class PetInfo extends L2GameServerPacket
{
	private final L2Summon _summon;
	private final int _x, _y, _z, _heading;
	private final boolean _isSummoned;
	private final int _mAtkSpd, _pAtkSpd;
	private final int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd;
	private int _flRunSpd;
	private int _flWalkSpd;
	private int _flyRunSpd;
	private int _flyWalkSpd;
	private final int _maxHp, _maxMp;
	private int _maxFed, _curFed;
	
	/**
	 * rev 478 dddddddddddddddddddffffdddcccccSSdddddddddddddddddddddddddddhc
	 * @param summon
	 */
	public PetInfo(L2Summon summon)
	{
		_summon = summon;
		_isSummoned = _summon.isShowSummonAnimation();
		_x = _summon.getX();
		_y = _summon.getY();
		_z = _summon.getZ();
		_heading = _summon.getHeading();
		_mAtkSpd = _summon.getMAtkSpd();
		_pAtkSpd = _summon.getPAtkSpd();
		_runSpd = _summon.getRunSpeed();
		_walkSpd = _summon.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
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
		writeByte(0xb1);
		writeInt(_summon.getSummonType());
		writeInt(_summon.getObjectId());
		writeInt(_summon.getTemplate().getTemplateId() + 1000000);
		writeInt(0); // 1=attackable
		
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
		writeInt(0);
		writeInt(_mAtkSpd);
		writeInt(_pAtkSpd);
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_swimRunSpd);
		writeInt(_swimWalkSpd);
		writeInt(_flRunSpd);
		writeInt(_flWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		
		writeDouble(1/* _cha.getProperMultiplier() */);
		writeDouble(1/* _cha.getAttackSpeedMultiplier() */);
		writeDouble(_summon.getTemplate().getCollisionRadius());
		writeDouble(_summon.getTemplate().getCollisionHeight());
		writeInt(0); // right hand weapon
		writeInt(0);
		writeInt(0); // left hand weapon
		writeByte(1); // name above char 1=true ... ??
		writeByte(_summon.isRunning() ? 1 : 0); // running=1
		writeByte(_summon.isInCombat() ? 1 : 0); // attacking 1=true
		writeByte(_summon.isAlikeDead() ? 1 : 0); // dead 1=true
		writeByte(_isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		writeString(_summon.getName());
		writeString(_summon.getTitle());
		writeInt(1);
		writeInt(_summon.getPvpFlag()); // 0 = white,2= purpleblink, if its greater then karma = purple
		writeInt(_summon.getKarma()); // hmm karma ??
		writeInt(_curFed); // how fed it is
		writeInt(_maxFed); // max fed it can be
		writeInt((int) _summon.getCurrentHp());// current hp
		writeInt(_maxHp);// max hp
		writeInt((int) _summon.getCurrentMp());// current mp
		writeInt(_maxMp);// max mp
		writeInt((int)_summon.getStat().getSp()); // sp
		writeInt(_summon.getLevel());// lvl
		writeLong(_summon.getStat().getExp());
		writeLong(_summon.getExpForThisLevel());// 0% absolute value
		writeLong(_summon.getExpForNextLevel());// 100% absoulte value
		writeInt(_summon instanceof L2PetInstance ? _summon.getInventory().getTotalWeight() : 0);// weight
		writeInt(_summon.getMaxLoad());// max weight it can carry
		writeInt(_summon.getPAtk(null));// patk
		writeInt(_summon.getPDef(null));// pdef
		writeInt(_summon.getMAtk(null, null));// matk
		writeInt(_summon.getMDef(null, null));// mdef
		writeInt(_summon.getAccuracy());// accuracy
		writeInt(_summon.getEvasionRate(null));// evasion
		writeInt(_summon.getCriticalHit(null, null));// critical
		writeInt(_runSpd);// speed
		writeInt(_summon.getPAtkSpd());// atkspeed
		writeInt(_summon.getMAtkSpd());// casting speed
		
		writeInt(0);// c2 abnormal visual effect... bleed=1; poison=2; poison & bleed=3; flame=4;
		int npcId = _summon.getTemplate().getId();
		
		if ((npcId >= 12526) && (npcId <= 12528))
		{
			writeShort(1);// c2 ride button
		}
		else
		{
			writeShort(0);
		}
		
		writeByte(0); // c2
		
		// Following allTemplates added in C4.
		writeShort(0); // ??
		writeByte(0); // team aura (1 = blue, 2 = red)
		writeInt(_summon.getSoulShotsPerHit()); // How many soulshots this servitor uses per hit
		writeInt(_summon.getSpiritShotsPerHit()); // How many spiritshots this servitor uses per hit
	}
}
