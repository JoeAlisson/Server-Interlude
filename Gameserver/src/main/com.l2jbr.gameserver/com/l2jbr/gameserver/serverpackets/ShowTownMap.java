package com.l2jbr.gameserver.serverpackets;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowTownMap extends L2GameServerPacket
{
	private final String _texture;
	private final int _x;
	private final int _y;

	public ShowTownMap(String texture, int x, int y)
	{
		_texture = texture;
		_x = x;
		_y = y;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xde);
		writeString(_texture);
		writeInt(_x);
		writeInt(_y);
	}
}
