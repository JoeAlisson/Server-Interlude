package org.l2j.gameserver.serverpackets;

/**
 * @author chris_00 opens the CommandChannel Information window
 */
public class ExOpenMPCC extends L2GameServerPacket {

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x25);
		
	}
}
