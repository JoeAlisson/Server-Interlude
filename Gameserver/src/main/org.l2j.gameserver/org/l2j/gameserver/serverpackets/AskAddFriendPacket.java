package org.l2j.gameserver.serverpackets;

public class AskAddFriendPacket extends L2GameServerPacket {

	private final String _requestorName;

	public AskAddFriendPacket(String requestorName)
	{
		_requestorName = requestorName;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x83);
		writeByte(0);
		writeString(_requestorName);
	}

    @Override
    protected int packetSize() {
        return _requestorName.length() * 2 + 6;
    }
}
