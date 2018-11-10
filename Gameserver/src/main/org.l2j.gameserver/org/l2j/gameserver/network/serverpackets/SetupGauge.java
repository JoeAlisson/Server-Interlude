package org.l2j.gameserver.network.serverpackets;

/**
 * sample 0000: 85 00 00 00 00 f0 1a 00 00
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class SetupGauge extends L2GameServerPacket
{
	public static final int BLUE = 0;
	public static final int RED = 1;
	public static final int CYAN = 2;
	
	private final int _dat1;
	private final int _time;
	
	public SetupGauge(int dat1, int time)
	{
		_dat1 = dat1;// color 0-blue 1-red 2-cyan 3-
		_time = time;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x6d);
		writeInt(_dat1);
		writeInt(_time);
		
		writeInt(_time); // c2
	}
}
