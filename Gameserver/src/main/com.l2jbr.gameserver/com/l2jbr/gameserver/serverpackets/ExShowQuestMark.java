package com.l2jbr.gameserver.serverpackets;

/**
 * @author Luca Baldi
 */
public class ExShowQuestMark extends L2GameServerPacket
{
	private final int _questId;
	
	public ExShowQuestMark(int questId)
	{
		_questId = questId;
	}

	@Override
	protected void writeImpl()
	{
		// TODO Auto-generated method stub
		writeByte(0xfe);
		writeShort(0x1a);
		writeInt(_questId);
	}
	
}
