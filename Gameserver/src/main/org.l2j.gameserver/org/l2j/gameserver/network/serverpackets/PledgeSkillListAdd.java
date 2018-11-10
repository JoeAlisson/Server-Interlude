package org.l2j.gameserver.network.serverpackets;

/**
 * Format: (ch) dd
 * @author -Wooden-
 */
public class PledgeSkillListAdd extends L2GameServerPacket
{
	private final int _id;
	private final int _lvl;
	
	public PledgeSkillListAdd(int id, int lvl)
	{
		_id = id;
		_lvl = lvl;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x3a);
		
		writeInt(_id);
		writeInt(_lvl);
	}
}