package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.datatables.ClanTable;
import org.l2j.gameserver.model.L2Clan;

/**
 * @author -Wooden-
 */
public class PledgeReceiveWarList extends L2GameServerPacket
{
	private final L2Clan _clan;
	private final int _tab;
	
	public PledgeReceiveWarList(L2Clan clan, int tab)
	{
		_clan = clan;
		_tab = tab;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x3e);
		
		writeInt(_tab); // type : 0 = Declared, 1 = Under Attack
		writeInt(0x00); // page
		writeInt(_tab == 0 ? _clan.getWarList().size() : _clan.getAttackerList().size());
		for (Integer i : _tab == 0 ? _clan.getWarList() : _clan.getAttackerList())
		{
			L2Clan clan = ClanTable.getInstance().getClan(i);
			if (clan == null)
			{
				continue;
			}
			
			writeString(clan.getName());
			writeInt(_tab); // ??
			writeInt(_tab); // ??
		}
	}
}
