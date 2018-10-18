package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Character;

/**
 * Format (ch)dddcccd d: cahacter oid d: time left d: fish hp c: c: c: 00 if fish gets damage 02 if fish regens d:
 * @author -Wooden-
 */
public class ExFishingHpRegen extends L2GameServerPacket
{
	private final L2Character _activeChar;
	private final int _time, _fishHP, _hpMode, _anim, _goodUse, _penalty, _hpBarColor;
	
	public ExFishingHpRegen(L2Character character, int time, int fishHP, int HPmode, int GoodUse, int anim, int penalty, int hpBarColor)
	{
		_activeChar = character;
		_time = time;
		_fishHP = fishHP;
		_hpMode = HPmode;
		_goodUse = GoodUse;
		_anim = anim;
		_penalty = penalty;
		_hpBarColor = hpBarColor;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x16);
		
		writeInt(_activeChar.getObjectId());
		writeInt(_time);
		writeInt(_fishHP);
		writeByte(_hpMode); // 0 = HP stop, 1 = HP raise
		writeByte(_goodUse); // 0 = none, 1 = success, 2 = failed
		writeByte(_anim); // Anim: 0 = none, 1 = reeling, 2 = pumping
		writeInt(_penalty); // Penalty
		writeByte(_hpBarColor); // 0 = normal hp bar, 1 = purple hp bar
		
	}

}