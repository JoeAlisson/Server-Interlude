package org.l2j.gameserver.network.serverpackets;

public class TutorialShowQuestionMark extends L2GameServerPacket
{
	private final int _blink;
	
	public TutorialShowQuestionMark(int blink)
	{
		_blink = blink; // this influences the blinking frequancy :S
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xa1);
		writeInt(_blink);
	}
}