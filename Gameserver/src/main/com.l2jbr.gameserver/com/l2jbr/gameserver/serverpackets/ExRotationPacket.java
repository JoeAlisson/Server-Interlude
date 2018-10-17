package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class ExRotationPacket extends L2GameServerPacket {

	private final int _charObjId;
	private final int _degree;

	// TODO Remove side
	public ExRotationPacket(L2PcInstance player, int degree, int side) {
		_charObjId = player.getObjectId();
		_degree = degree;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0xFE);
		writeShort(0xC2);
		writeInt(_charObjId);
		writeInt(_degree);
	}

}
