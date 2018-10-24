package org.l2j.gameserver.serverpackets;

public class TutorialShowHtml extends L2GameServerPacket
{
	private final String _html;
	
	public TutorialShowHtml(String html)
	{
		_html = html;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xa0);
		writeString(_html);
	}
}