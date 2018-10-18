package com.l2jbr.gameserver.serverpackets;

public class MagicSkillCanceld extends L2GameServerPacket
{
	private final int _objectId;
	
	public MagicSkillCanceld(int objectId)
	{
		_objectId = objectId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x49);
		writeInt(_objectId);
	}
}
