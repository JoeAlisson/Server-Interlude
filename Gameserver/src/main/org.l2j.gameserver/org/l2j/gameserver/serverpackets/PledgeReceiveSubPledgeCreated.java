package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Clan.SubPledge;

/**
 * @author -Wooden-
 */
public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private final SubPledge _subPledge;

	public PledgeReceiveSubPledgeCreated(SubPledge subPledge)
	{
		_subPledge = subPledge;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x3f);
		
		writeInt(0x01);
		writeInt(_subPledge.getId());
		writeString(_subPledge.getName());
		writeString(_subPledge.getLeaderName());
	}

}
