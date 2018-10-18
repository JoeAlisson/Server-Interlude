package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch
 * @author devScarlet & mrTJO
 */
public class ExShowSlideshowKamael extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x5b);
	}
}
