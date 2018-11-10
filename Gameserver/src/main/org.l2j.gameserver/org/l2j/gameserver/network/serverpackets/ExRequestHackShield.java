package org.l2j.gameserver.network.serverpackets;

/**
 * Format: ch Trigger packet
 * @author KenM
 */
public class ExRequestHackShield extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x48);
	}

}
