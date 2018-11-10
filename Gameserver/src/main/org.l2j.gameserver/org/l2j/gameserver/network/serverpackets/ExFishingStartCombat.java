package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Character;

/**
 * Format (ch)dddcc
 * @author -Wooden-
 */
public class ExFishingStartCombat extends L2GameServerPacket
{
	private static final String _S__FE_15_EXFISHINGSTARTCOMBAT = "[S] FE:15 ExFishingStartCombat";
	private final L2Character _activeChar;
	private final int _time, _hp;
	private final int _lureType, _deceptiveMode, _mode;
	
	public ExFishingStartCombat(L2Character character, int time, int hp, int mode, int lureType, int deceptiveMode)
	{
		_activeChar = character;
		_time = time;
		_hp = hp;
		_mode = mode;
		_lureType = lureType;
		_deceptiveMode = deceptiveMode;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x15);
		
		writeInt(_activeChar.getObjectId());
		writeInt(_time);
		writeInt(_hp);
		writeByte(_mode); // mode: 0 = resting, 1 = fighting
		writeByte(_lureType); // 0 = newbie lure, 1 = normal lure, 2 = night lure
		writeByte(_deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
	}
}