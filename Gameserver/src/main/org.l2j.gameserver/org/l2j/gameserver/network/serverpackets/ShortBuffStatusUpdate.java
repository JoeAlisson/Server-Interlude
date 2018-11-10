package org.l2j.gameserver.network.serverpackets;

public class ShortBuffStatusUpdate extends L2GameServerPacket
{
	private final int _skillId;
	private final int _skillLvl;
	private final int _duration;
	
	public ShortBuffStatusUpdate(int skillId, int skillLvl, int duration)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
		_duration = duration;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xF4);
		writeInt(_skillId);
		writeInt(_skillLvl);
		writeInt(_duration);
	}
}
