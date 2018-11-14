package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.SevenSigns;

public class ShowMiniMap extends L2GameServerPacket {
	private final int _mapId;

	public ShowMiniMap(int mapId) {
		_mapId = mapId;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0xA3);
		writeInt(_mapId);
		writeByte(SevenSigns.getInstance().getCurrentPeriod());
	}
}
