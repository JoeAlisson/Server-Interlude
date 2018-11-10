package org.l2j.gameserver.network.serverpackets;

/**
 * sample
 * <p>
 * 4c 01 00 00 00
 * <p>
 * format cd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class JoinParty extends L2GameServerPacket
{
	private final int _response;

	public JoinParty(int response)
	{
		_response = response;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x3a);
		
		writeInt(_response);
	}
}
