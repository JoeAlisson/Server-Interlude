package org.l2j.gameserver.network.serverpackets;

public class AutoAttackStopPacket extends L2GameServerPacket {

	private final int _targetObjId;

	public AutoAttackStopPacket(int targetObjId)
	{
		_targetObjId = targetObjId;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x26);
		writeInt(_targetObjId);
	}

	@Override
	protected int packetSize() {
		return 7;
	}
}
