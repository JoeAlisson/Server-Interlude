package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;


public class ManagePledgePower extends L2GameServerPacket
{
	private final int _action;
	private final L2Clan _clan;
	private final int _rank;
	private int _privs;
	
	public ManagePledgePower(L2Clan clan, int action, int rank)
	{
		_clan = clan;
		_action = action;
		_rank = rank;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_action == 1)
		{
			_privs = _clan.getRankPrivs(_rank);
		}
		else
		{
			return;
			/*
			 * if (L2World.getInstance().findObject(_clanId) == null) return; privs = ((L2PcInstance)L2World.getInstance().findObject(_clanId)).getClanPrivileges();
			 */
		}
		writeByte(0x30);
		writeInt(0);
		writeInt(0);
		writeInt(_privs);
	}
}
