package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Character;

/**
 * Format (ch)ddddd
 * @author -Wooden-
 */
public class ExFishingStart extends L2GameServerPacket
{
	private final L2Character _activeChar;
	private final int _x, _y, _z, _fishType;
	
	public ExFishingStart(L2Character character, int fishType, int x, int y, int z, boolean isNightLure)
	{
		_activeChar = character;
		_fishType = fishType;
		_x = x;
		_y = y;
		_z = z;
	}
	

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x13);
		writeInt(_activeChar.getObjectId());
		writeInt(_fishType); // fish type
		writeInt(_x); // x poisson
		writeInt(_y); // y poisson
		writeInt(_z); // z poisson
		writeByte(0x00); // night lure
		writeByte(0x00); // ??
		writeByte(((_fishType >= 7) && (_fishType <= 9)) ? 0x01 : 0x00); // 0 = day lure 1 = night lure
		writeByte(0x00);
	}
	
}