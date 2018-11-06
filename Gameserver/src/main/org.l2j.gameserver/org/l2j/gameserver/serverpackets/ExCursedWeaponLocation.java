package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.position.Position;
import org.l2j.gameserver.util.Point3D;

import java.util.List;

public class ExCursedWeaponLocation extends L2GameServerPacket {

	private final List<CursedWeaponInfo> _cursedWeaponInfo;
	
	public ExCursedWeaponLocation(List<CursedWeaponInfo> cursedWeaponInfo)
	{
		_cursedWeaponInfo = cursedWeaponInfo;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x46);
		
		if (!_cursedWeaponInfo.isEmpty())
		{
			writeInt(_cursedWeaponInfo.size());
			for (CursedWeaponInfo w : _cursedWeaponInfo)
			{
				writeInt(w.id);
				writeInt(w.activated);
				
				writeInt(w.pos.getX());
				writeInt(w.pos.getY());
				writeInt(w.pos.getZ());
			}
		}
		else
		{
			writeInt(0);
			writeInt(0);
		}
	}

	public static class CursedWeaponInfo
	{
		public Position pos;
		public int id;
		public int activated; // 0 - not activated ? 1 - activated
		
		public CursedWeaponInfo(Position p, int ID, int status) {
			pos = p;
			id = ID;
			activated = status;
		}
	}
}