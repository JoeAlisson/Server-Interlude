package org.l2j.gameserver.network.serverpackets;

public class RadarControl extends L2GameServerPacket
{
	private final int _showRadar;
	private final int _type;
	private final int _x;
	private final int _y;
	private final int _z;

	public RadarControl(int showRadar, int type, int x, int y, int z)
	{
		_showRadar = showRadar; // showRader?? 0 = showradar; 1 = delete radar;
		_type = type; // radar type??
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xEB);
		writeInt(_showRadar);
		writeInt(_type); // maybe type
		writeInt(_x); // x
		writeInt(_y); // y
		writeInt(_z); // z
	}
}
