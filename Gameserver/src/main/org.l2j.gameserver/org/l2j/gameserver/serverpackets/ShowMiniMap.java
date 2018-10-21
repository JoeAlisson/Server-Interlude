package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.SevenSigns;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowMiniMap extends L2GameServerPacket
{
	private final int _mapId;

	public ShowMiniMap(int mapId)
	{
		_mapId = mapId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x9d);
		writeInt(_mapId);
		writeInt(SevenSigns.getInstance().getCurrentPeriod());
	}
}
