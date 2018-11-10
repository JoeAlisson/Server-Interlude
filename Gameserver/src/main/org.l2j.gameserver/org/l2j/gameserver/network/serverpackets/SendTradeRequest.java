package org.l2j.gameserver.network.serverpackets;

public class SendTradeRequest extends L2GameServerPacket
{
	private final int _senderID;
	
	public SendTradeRequest(int senderID)
	{
		_senderID = senderID;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x5e);
		writeInt(_senderID);
	}
}
