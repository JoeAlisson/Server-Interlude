package org.l2j.gameserver.serverpackets;

public class PledgeShowMemberListDelete extends L2GameServerPacket
{
	private final String _player;
	
	public PledgeShowMemberListDelete(String playerName)
	{
		_player = playerName;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x56);
		writeString(_player);
	}
}
