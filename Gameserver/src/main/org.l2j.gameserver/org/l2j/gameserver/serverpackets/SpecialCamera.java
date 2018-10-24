package org.l2j.gameserver.serverpackets;

public class SpecialCamera extends L2GameServerPacket
{
	private final int _id;
	private final int _dist;
	private final int _yaw;
	private final int _pitch;
	private final int _time;
	private final int _duration;
	
	public SpecialCamera(int id, int dist, int yaw, int pitch, int time, int duration)
	{
		_id = id;
		_dist = dist;
		_yaw = yaw;
		_pitch = pitch;
		_time = time;
		_duration = duration;
	}
	
	@Override
	public void writeImpl()
	{
		writeByte(0xc7);
		writeInt(_id);
		writeInt(_dist);
		writeInt(_yaw);
		writeInt(_pitch);
		writeInt(_time);
		writeInt(_duration);
	}
}
