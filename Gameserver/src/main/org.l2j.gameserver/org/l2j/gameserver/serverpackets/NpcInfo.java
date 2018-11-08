package org.l2j.gameserver.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;

public class NpcInfo extends L2GameServerPacket
{
	// ddddddddddddddddddffffdddcccccSSddd dddddc
	// ddddddddddddddddddffffdddcccccSSddd dddddccffd
	private final L2Character _activeChar;
	private final int _x, _y, _z, _heading;
	private final int _idTemplate;
	private final boolean _isAttackable, _isSummoned;
	private final int _mAtkSpd, _pAtkSpd;
	private final int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd;
	private int _flRunSpd;
	private int _flWalkSpd;
	private int _flyRunSpd;
	private int _flyWalkSpd;
	private final int _rhand, _lhand;
	private final float _collisionHeight, _collisionRadius;
	private String _name = "";
	private String _title = "";
	

	public NpcInfo(L2NpcInstance cha, L2Character attacker)
	{
		_activeChar = cha;
		_idTemplate = cha.getNpcTemplateId();
		_isAttackable = cha.isAutoAttackable(attacker);
		_rhand = cha.getRightHandItem();
		_lhand = cha.getLeftHandItem();
		_isSummoned = false;
		_collisionHeight = cha.getCollisionHeight();
		_collisionRadius = cha.getCollisionRadius();
		if (cha.isServerSideName())
		{
			_name = cha.getName();
		}
		
		if (Config.L2JMOD_CHAMPION_ENABLE && cha.isChampion())
		{
			_title = ("Champion");
		}
		else if (cha.isServerSideTitle())
		{
			_title = cha.getTitle();
		}
		else
		{
			_title = cha.getTitle();
		}
		
		if (Config.SHOW_NPC_LVL && (_activeChar instanceof L2MonsterInstance))
		{
			String t = "Lv " + cha.getLevel() + (cha.getAggroRange() > 0 ? "*" : "");
			if (_title != null)
			{
				t += " " + _title;
			}
			
			_title = t;
		}
		
		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _activeChar.getRunSpeed();
		_walkSpd = _activeChar.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}
	
	public NpcInfo(L2Summon cha, L2Character attacker)
	{
		_activeChar = cha;
		_idTemplate = cha.getNpcTemplateId();
		_isAttackable = cha.isAutoAttackable(attacker); // (cha.getKarma() > 0);
		_rhand = 0;
		_lhand = 0;
		_isSummoned = cha.isShowSummonAnimation();
		_collisionHeight = _activeChar.getCollisionHeight();
		_collisionRadius = _activeChar.getCollisionRadius();
		if (cha.isServerSideName() || (cha instanceof L2PetInstance))
		{
			_name = _activeChar.getName();
			_title = cha.getTitle();
		}
		
		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _activeChar.getRunSpeed();
		_walkSpd = _activeChar.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_activeChar instanceof L2Summon)
		{
			if ((((L2Summon) _activeChar).getOwner() != null) && ((L2Summon) _activeChar).getOwner().isInvisible())
			{
				return;
			}
		}
		writeByte(0x16);
		writeInt(_activeChar.getObjectId());
		writeInt(_idTemplate + 1000000); // npctype id
		writeInt(_isAttackable ? 1 : 0);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
		writeInt(0x00);
		writeInt(_mAtkSpd);
		writeInt(_pAtkSpd);
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_swimRunSpd/* 0x32 */); // swimspeed
		writeInt(_swimWalkSpd/* 0x32 */); // swimspeed
		writeInt(_flRunSpd);
		writeInt(_flWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		writeDouble(1.1/* _activeChar.getProperMultiplier() */);
		// writeDouble(1/*_activeChar.getAttackSpeedMultiplier()*/);
		writeDouble(_pAtkSpd / 277.478340719);
		writeDouble(_collisionRadius);
		writeDouble(_collisionHeight);
		writeInt(_rhand); // right hand weapon
		writeInt(0);
		writeInt(_lhand); // left hand weapon
		writeByte(1); // name above char 1=true ... ??
		writeByte(_activeChar.isRunning() ? 1 : 0);
		writeByte(_activeChar.isInCombat() ? 1 : 0);
		writeByte(_activeChar.isAlikeDead() ? 1 : 0);
		writeByte(_isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		writeString(_name);
		writeString(_title);
		writeInt(0);
		writeInt(0);
		writeInt(0000); // hmm karma ??
		
		writeInt(_activeChar.getAbnormalEffect()); // C2
		writeInt(0000); // C2
		writeInt(0000); // C2
		writeInt(0000); // C2
		writeInt(0000); // C2
		writeByte(0000); // C2
		
		writeByte(0x00); // C3 team circle 1-blue, 2-red
		writeDouble(_collisionRadius);
		writeDouble(_collisionHeight);
		writeInt(0x00); // C4
		writeInt(0x00); // C6
	}
}
