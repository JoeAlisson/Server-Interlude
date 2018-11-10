package org.l2j.gameserver.network.serverpackets;

/**
 * Format: ch (trigger)
 * @author KenM
 */
public class ExShowAdventurerGuideBook extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x37);
	}
}
