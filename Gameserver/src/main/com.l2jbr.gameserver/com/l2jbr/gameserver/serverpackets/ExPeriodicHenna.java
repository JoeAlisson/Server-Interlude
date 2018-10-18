package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.database.Henna;

public class ExPeriodicHenna extends L2GameServerPacket
{
	private final Henna _henna;
	private final boolean _active = false;

	public ExPeriodicHenna(L2PcInstance player) {

		_henna = null;
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x164);
		if(_henna != null) {
			writeInt(0);	// Premium symbol ID
			writeInt(0);	// Premium symbol left time
			writeInt(0);	// Premium symbol active
		}
		else
		{
			writeInt(0x00);	// Premium symbol ID
			writeInt(0x00);	// Premium symbol left time
			writeInt(0x00);	// Premium symbol active
		}
	}
}
