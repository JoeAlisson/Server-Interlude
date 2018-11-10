package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author zabbix
 */
public class PartyMemberPosition extends L2GameServerPacket
{
	private final L2Party _party;
	
	public PartyMemberPosition(L2PcInstance actor)
	{
		_party = actor.getParty();
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xa7);
		writeInt(_party.getMemberCount());
		
		for (L2PcInstance pm : _party.getPartyMembers())
		{
			if (pm == null)
			{
				continue;
			}
			
			writeInt(pm.getObjectId());
			writeInt(pm.getX());
			writeInt(pm.getY());
			writeInt(pm.getZ());
		}
	}
}
