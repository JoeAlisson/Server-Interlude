
package com.l2jbr.gameserver.serverpackets;

public class ExDuelAskStart extends L2GameServerPacket {

	private final String _requestorName;
	private final int _partyDuel;
	
	public ExDuelAskStart(String requestor, int partyDuel)
	{
		_requestorName = requestor;
		_partyDuel = partyDuel;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x4b);
		
		writeString(_requestorName);
		writeInt(_partyDuel);
	}
}
