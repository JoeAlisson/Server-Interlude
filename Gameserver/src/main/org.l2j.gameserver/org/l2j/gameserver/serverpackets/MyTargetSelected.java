package org.l2j.gameserver.serverpackets;

/**
 * <p>
 * sample bf 73 5d 30 49 01 00
 * <p>
 * format dh (objectid, color)
 * <p>
 * color -xx -> -9 red
 * <p>
 * -8 -> -6 light-red
 * <p>
 * -5 -> -3 yellow
 * <p>
 * -2 -> 2 white
 * <p>
 * 3 -> 5 green
 * <p>
 * 6 -> 8 light-blue
 * <p>
 * 9 -> xx blue
 * <p>
 * <p>
 * usually the color equals the level difference to the selected target
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class MyTargetSelected extends L2GameServerPacket
{
	private final int _objectId;
	private final int _color;
	
	/**
	 * @param objectId object Id of the target
	 * @param color color code
	 */
	public MyTargetSelected(int objectId, int color)
	{
		_objectId = objectId;
		_color = color;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xa6);
		writeInt(_objectId);
		writeShort(_color);
	}
}
