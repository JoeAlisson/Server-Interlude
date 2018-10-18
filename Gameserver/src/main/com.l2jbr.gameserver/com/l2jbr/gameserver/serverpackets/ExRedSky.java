package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch d
 * @author KenM
 */
public class ExRedSky extends L2GameServerPacket
{
	private final int _duration;
	
	public ExRedSky(int duration)
	{
		_duration = duration;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x40);
		writeInt(_duration);
	}

}
