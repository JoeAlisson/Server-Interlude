package com.l2jbr.gameserver.serverpackets;

public class LeaveWorld extends L2GameServerPacket
{
	private static final String _S__96_LEAVEWORLD = "[S] 7e LeaveWorld";
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x84);
	}

	@Override
	public String getType()
	{
		return _S__96_LEAVEWORLD;
	}

	@Override
	protected int packetSize() {
		return 3;
	}
}
