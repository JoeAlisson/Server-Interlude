package org.l2j.gameserver.network.serverpackets;

/**
 * @author zabbix Lets drink to code!
 */
public class GameGuardQuery extends L2GameServerPacket
{
	@Override
	public void runImpl()
	{
		// Lets make user as gg-unauthorized
		// We will set him as ggOK after reply fromclient
		// or kick
		getClient().setGameGuardOk(false);
	}
	
	@Override
	public void writeImpl()
	{
		writeByte(0xf9);
	}

}
