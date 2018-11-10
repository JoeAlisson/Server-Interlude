package org.l2j.gameserver.network.serverpackets;

public class StopPledgeWar extends L2GameServerPacket
{
	private final String _pledgeName;
	private final String _playerName;
	
	public StopPledgeWar(String pledge, String charName)
	{
		_pledgeName = pledge;
		_playerName = charName;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x67);
		writeString(_pledgeName);
		writeString(_playerName);
	}
}