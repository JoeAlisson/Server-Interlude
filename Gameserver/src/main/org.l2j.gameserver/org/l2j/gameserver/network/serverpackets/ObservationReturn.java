package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class ObservationReturn extends L2GameServerPacket
{
	// ddSS
	private final L2PcInstance _activeChar;

	public ObservationReturn(L2PcInstance observer)
	{
		_activeChar = observer;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xe0);
		writeInt(_activeChar.getObsX());
		writeInt(_activeChar.getObsY());
		writeInt(_activeChar.getObsZ());
	}

}
