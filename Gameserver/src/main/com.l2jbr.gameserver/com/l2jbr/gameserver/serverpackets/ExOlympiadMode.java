package com.l2jbr.gameserver.serverpackets;


public class ExOlympiadMode extends L2GameServerPacket
{
	// chc
	private static final String _S__FE_2B_OLYMPIADMODE = "[S] FE:2B ExOlympiadMode";
	private static int _mode;
	
	/**
	 * @param mode (0 = return, 3 = spectate)
	 */
	public ExOlympiadMode(int mode)
	{
		_mode = mode;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x2b);
		writeByte(_mode);
	}
}
