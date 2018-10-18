package com.l2jbr.gameserver.serverpackets;

public class StartPledgeWar extends L2GameServerPacket
{
	private final String _pledgeName;
	private final String _playerName;
	
	public StartPledgeWar(String pledge, String charName)
	{
		_pledgeName = pledge;
		_playerName = charName;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x65);
		writeString(_playerName);
		writeString(_pledgeName);
	}
}