package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Character;

/**
 * format dd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class FinishRotation extends L2GameServerPacket
{
	private static final String _S__78_FINISHROTATION = "[S] 63 FinishRotation";
	private final int _heading;
	private final int _charObjId;
	
	public FinishRotation(L2Character cha)
	{
		_charObjId = cha.getObjectId();
		_heading = cha.getHeading();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x63);
		writeInt(_charObjId);
		writeInt(_heading);
	}
	
}
