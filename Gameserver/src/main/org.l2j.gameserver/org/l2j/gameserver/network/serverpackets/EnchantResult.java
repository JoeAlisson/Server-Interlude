package org.l2j.gameserver.network.serverpackets;

public class EnchantResult extends L2GameServerPacket {

	private final int _unknown;
	
	public EnchantResult(int unknown)
	{
		_unknown = unknown;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x81);
		writeInt(_unknown);
	}

}