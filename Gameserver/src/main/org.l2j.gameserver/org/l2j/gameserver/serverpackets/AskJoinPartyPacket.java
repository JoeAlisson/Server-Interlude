package org.l2j.gameserver.serverpackets;

public class AskJoinPartyPacket extends L2GameServerPacket {

	private final String _requestorName;
	private final int _itemDistribution;

	public AskJoinPartyPacket(String requestorName, int itemDistribution) {
		_requestorName = requestorName;
		_itemDistribution = itemDistribution;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x39);
		writeString(_requestorName);
		writeInt(_itemDistribution);
	}

	@Override
	protected int packetSize() {
		return _requestorName.length() * 2 + 9;
	}
}
