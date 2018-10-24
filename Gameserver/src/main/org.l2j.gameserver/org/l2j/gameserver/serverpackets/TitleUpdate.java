package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author devScarlet
 */
public class TitleUpdate extends L2GameServerPacket
{
	private final String _title;
	private final int _objectId;
	
	public TitleUpdate(L2PcInstance cha)
	{
		_objectId = cha.getObjectId();
		_title = cha.getTitle();
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xcc);
		writeInt(_objectId);
		writeString(_title);
	}
}
