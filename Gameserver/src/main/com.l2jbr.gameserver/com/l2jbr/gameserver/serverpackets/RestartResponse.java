package com.l2jbr.gameserver.serverpackets;

public class RestartResponse extends L2GameServerPacket
{
	private final String _message;
	
	public RestartResponse()
	{
		_message = "ok merong~ khaha";
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x5f);
		
		writeInt(0x01); // 01-ok
		writeString(_message);
	}
}
