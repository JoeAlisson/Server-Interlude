package org.l2j.gameserver.serverpackets;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowCalculator extends L2GameServerPacket
{
	private final int _calculatorId;

	public ShowCalculator(int calculatorId)
	{
		_calculatorId = calculatorId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xdc);
		writeInt(_calculatorId);
	}
}