package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;

public class StopRotation extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _degree;
	
	public StopRotation(L2Character player, int degree)
	{
		_charObjId = player.getObjectId();
		_degree = degree;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x63);
		writeInt(_charObjId);
		writeInt(_degree);
	}
}
