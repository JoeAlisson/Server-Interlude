package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Character;

public class ChangeMoveType extends L2GameServerPacket {

	public static final int WALK = 0;
	public static final int RUN = 1;
	
	private final int _charObjId;
	private final boolean _running;
	
	public ChangeMoveType(L2Character character) {
		_charObjId = character.getObjectId();
		_running = character.isRunning();
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x2e);
		writeInt(_charObjId);
		writeInt(_running ? RUN : WALK);
		writeInt(0); // c2
	}

}
