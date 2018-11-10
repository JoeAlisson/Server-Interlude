package org.l2j.gameserver.network.serverpackets;

/**
 * @author chris_00 close the CommandChannel Information window
 */
public class ExCloseMPCC extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x26);
	}
}
