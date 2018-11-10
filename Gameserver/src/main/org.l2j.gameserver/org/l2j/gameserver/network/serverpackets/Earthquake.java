package org.l2j.gameserver.network.serverpackets;

public class Earthquake extends L2GameServerPacket
{

	private final int _x;
	private final int _y;
	private final int _z;
	private final int _intensity;
	private final int _duration;

	public Earthquake(int x, int y, int z, int intensity, int duration)
	{
		_x = x;
		_y = y;
		_z = z;
		_intensity = intensity;
		_duration = duration;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xc4);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_intensity);
		writeInt(_duration);
		writeInt(0x00); // Unknown
	}
}
