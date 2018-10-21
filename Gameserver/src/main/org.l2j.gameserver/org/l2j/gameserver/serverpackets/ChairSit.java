package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class ChairSit extends L2GameServerPacket {

	private final L2PcInstance _activeChar;
	private final int _staticObjectId;

	public ChairSit(L2PcInstance player, int staticObjectId) {
		_activeChar = player;
		_staticObjectId = staticObjectId;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0xe1);
		writeInt(_activeChar.getObjectId());
		writeInt(_staticObjectId);
	}

}
