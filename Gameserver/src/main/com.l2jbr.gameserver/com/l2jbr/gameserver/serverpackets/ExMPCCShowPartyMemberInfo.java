package com.l2jbr.gameserver.serverpackets;


/**
 * Format: ch d[Sdd]
 * @author KenM
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x4a);
		
		// TODO this packet has a list, so im not going to add temp vars ^^
	}
}
