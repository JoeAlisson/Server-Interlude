package org.l2j.gameserver.network.serverpackets;

public class PlaySound extends L2GameServerPacket
{
	private final int _unknown1;
	private final String _soundFile;
	private final int _unknown3;
	private final int _unknown4;
	private final int _unknown5;
	private final int _unknown6;
	private final int _unknown7;
	
	public PlaySound(String soundFile)
	{
		_unknown1 = 0;
		_soundFile = soundFile;
		_unknown3 = 0;
		_unknown4 = 0;
		_unknown5 = 0;
		_unknown6 = 0;
		_unknown7 = 0;
	}
	
	public PlaySound(int unknown1, String soundFile, int unknown3, int unknown4, int unknown5, int unknown6, int unknown7)
	{
		_unknown1 = unknown1;
		_soundFile = soundFile;
		_unknown3 = unknown3;
		_unknown4 = unknown4;
		_unknown5 = unknown5;
		_unknown6 = unknown6;
		_unknown7 = unknown7;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x98);
		writeInt(_unknown1); // unknown 0 for quest and ship;
		writeString(_soundFile);
		writeInt(_unknown3); // unknown 0 for quest; 1 for ship;
		writeInt(_unknown4); // 0 for quest; objectId of ship
		writeInt(_unknown5); // x
		writeInt(_unknown6); // y
		writeInt(_unknown7); // z
	}
}
