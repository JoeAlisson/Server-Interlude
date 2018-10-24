package org.l2j.gameserver.serverpackets;

public class Snoop extends L2GameServerPacket
{
	private final int _convoId;
	private final String _name;
	private final int _type;
	private final String _speaker;
	private final String _msg;
	
	public Snoop(int id, String name, int type, String speaker, String msg)
	{
		_convoId = id;
		_name = name;
		_type = type;
		_speaker = speaker;
		_msg = msg;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xd5);
		
		writeInt(_convoId);
		writeString(_name);
		writeInt(0x00); // ??
		writeInt(_type);
		writeString(_speaker);
		writeString(_msg);
	}
}