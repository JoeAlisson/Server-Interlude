package com.l2jbr.gameserver.serverpackets;

public class ObservationMode extends L2GameServerPacket
{
	private final int _x, _y, _z;

	public ObservationMode(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xdf);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeByte(0x00);
		writeByte(0xc0);
		writeByte(0x00);
	}
}
