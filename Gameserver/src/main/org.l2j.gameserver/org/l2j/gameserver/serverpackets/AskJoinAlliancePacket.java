package org.l2j.gameserver.serverpackets;

public class AskJoinAlliancePacket extends L2GameServerPacket {

	private final String _requestorName;
	private final int _requestorObjId;

	public AskJoinAlliancePacket(int requestorObjId, String requestorName) {
		_requestorName = requestorName;
		_requestorObjId = requestorObjId;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0xBB);
		writeInt(_requestorObjId);
		writeString(_requestorName);
		writeString("");
		writeString("");
	}

	@Override
	protected int packetSize() {
		return _requestorName.length() * 2 + 13;
	}
}
