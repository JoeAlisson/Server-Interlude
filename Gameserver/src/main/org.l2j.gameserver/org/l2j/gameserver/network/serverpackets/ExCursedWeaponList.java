package org.l2j.gameserver.network.serverpackets;

import java.util.List;

public class ExCursedWeaponList extends L2GameServerPacket {

	private final List<Integer> _cursedWeaponIds;
	
	public ExCursedWeaponList(List<Integer> cursedWeaponIds)
	{
		_cursedWeaponIds = cursedWeaponIds;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x45);
		
		writeInt(_cursedWeaponIds.size());
		for (Integer i : _cursedWeaponIds)
		{
			writeInt(i.intValue());
		}
	}

}