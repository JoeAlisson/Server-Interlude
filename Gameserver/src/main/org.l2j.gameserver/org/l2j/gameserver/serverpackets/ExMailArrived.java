package org.l2j.gameserver.serverpackets;

/**
 * Fromat: (ch) (just a trigger)
 * @author -Wooden-
 */
public class ExMailArrived extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x2d);
		
	}
}
