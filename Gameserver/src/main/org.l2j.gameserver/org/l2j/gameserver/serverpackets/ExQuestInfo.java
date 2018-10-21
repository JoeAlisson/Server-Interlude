package org.l2j.gameserver.serverpackets;

/**
 * This class ...
 * @version $Revision: $ $Date: $
 * @author Luca Baldi
 */
public class ExQuestInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x19);
	}
}
