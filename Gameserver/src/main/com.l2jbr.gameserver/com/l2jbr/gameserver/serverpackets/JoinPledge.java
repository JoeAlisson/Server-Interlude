package com.l2jbr.gameserver.serverpackets;

public class JoinPledge extends L2GameServerPacket
{
	private static final String _S__45_JOINPLEDGE = "[S] 33 JoinPledge";
	
	private final int _pledgeId;
	
	public JoinPledge(int pledgeId)
	{
		_pledgeId = pledgeId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x33);
		
		writeInt(_pledgeId);
	}
}
