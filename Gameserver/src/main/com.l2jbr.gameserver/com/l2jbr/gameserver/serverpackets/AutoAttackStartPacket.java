package com.l2jbr.gameserver.serverpackets;

public class AutoAttackStartPacket extends L2GameServerPacket {

	private final int _targetObjId;

	public AutoAttackStartPacket(int targetId)
	{
		_targetObjId = targetId;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x25);
		writeInt(_targetObjId);
	}

	@Override
	protected int packetSize() {
		return 7;
	}
}
