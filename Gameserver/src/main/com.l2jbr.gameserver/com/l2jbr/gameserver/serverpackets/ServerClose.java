package com.l2jbr.gameserver.serverpackets;

/**
 * @author devScarlet & mrTJO
 */
public class ServerClose extends L2GameServerPacket
{
	public static final ServerClose STATIC_PACKET = new ServerClose();
	

	@Override
	protected void writeImpl()
	{
		writeByte(0xB0);
	}
}
