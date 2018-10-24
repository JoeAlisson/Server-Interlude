package org.l2j.gameserver.serverpackets;

/**
 * format (c) dd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:40 $
 */
public class SetSummonRemainTime extends L2GameServerPacket
{
	private final int _maxTime;
	private final int _remainingTime;
	
	public SetSummonRemainTime(int maxTime, int remainingTime)
	{
		_remainingTime = remainingTime;
		_maxTime = maxTime;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xd1);
		writeInt(_maxTime);
		writeInt(_remainingTime);
	}

}
