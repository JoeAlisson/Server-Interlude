package org.l2j.gameserver.network.serverpackets;

public class SendTradeDone extends L2GameServerPacket
{
	private final int _num;
	
	public SendTradeDone(int num)
	{
		_num = num;
	}

	@Override
	protected final void writeImpl()
	{
		writeByte(0x22);
		writeInt(_num);
	}
}