package com.l2jbr.gameserver.serverpackets;

public final class ActionFailed extends L2GameServerPacket {
	private static final String _S__35_ACTIONFAILED = "[S] 0x1F ActionFailed";
	
	@Override
	protected void writeImpl()
	{
		writeByte(0x1F);
	}

	@Override
	public String getType()
	{
		return _S__35_ACTIONFAILED;
	}

	@Override
	protected int packetSize() {
		return 3;
	}
}
