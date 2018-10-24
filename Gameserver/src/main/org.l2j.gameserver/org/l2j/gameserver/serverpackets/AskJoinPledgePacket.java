package org.l2j.gameserver.serverpackets;

public class AskJoinPledgePacket extends L2GameServerPacket  {

	private final int _requestorObjId;
	private final String _pledgeName;
	
	public AskJoinPledgePacket(int requestorObjId, String pledgeName) {
		_requestorObjId = requestorObjId;
		_pledgeName = pledgeName;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x2C);
		writeInt(_requestorObjId);
		writeString(_pledgeName);
	}

	@Override
	protected int packetSize() {
		return _pledgeName.length() * 2 + 9;
	}
}
