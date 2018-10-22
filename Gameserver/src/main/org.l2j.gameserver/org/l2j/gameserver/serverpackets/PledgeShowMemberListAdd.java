package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class PledgeShowMemberListAdd extends L2GameServerPacket
{
	private String _name;
	private int _lvl;
	private int _classId;
	private int _isOnline;
	private int _pledgeType;
	
	public PledgeShowMemberListAdd(L2PcInstance player)
	{
		_name = player.getName();
		_lvl = player.getLevel();
		_classId = player.getPlayerClass().getId();
		_isOnline = (player.isOnline() ? player.getObjectId() : 0);
		_pledgeType = player.getPledgeType();
	}
	
	public PledgeShowMemberListAdd(L2ClanMember cm)
	{
		try
		{
			_name = cm.getName();
			_lvl = cm.getLevel();
			_classId = cm.getClassId();
			_isOnline = (cm.isOnline() ? cm.getObjectId() : 0);
			_pledgeType = cm.getPledgeType();
		}
		catch (Exception e)
		{
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x55);
		writeString(_name);
		writeInt(_lvl);
		writeInt(_classId);
		writeInt(0);
		writeInt(1);
		writeInt(_isOnline); // 1=online 0=offline
		writeInt(_pledgeType);
	}
}
