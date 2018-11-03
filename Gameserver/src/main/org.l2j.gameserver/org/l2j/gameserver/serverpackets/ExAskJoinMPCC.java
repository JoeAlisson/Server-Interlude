package org.l2j.gameserver.serverpackets;

/**
 * @author chris_00 Asks the reader to join a CC
 */
public class ExAskJoinMPCC extends L2GameServerPacket {
	private final String _requestorName;
	

	public ExAskJoinMPCC(String requestorName)
	{
		_requestorName = requestorName;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x27);
		writeString(_requestorName); // name of CCLeader
	}
}
