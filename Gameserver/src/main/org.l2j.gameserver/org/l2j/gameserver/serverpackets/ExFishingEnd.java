package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: (ch) dc d: character object id c: 1 if won 0 if failed
 * @author -Wooden-
 */
public class ExFishingEnd extends L2GameServerPacket
{
	private final boolean _win;
	L2Character _activeChar;
	
	public ExFishingEnd(boolean win, L2PcInstance character)
	{
		_win = win;
		_activeChar = character;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x14);
		writeInt(_activeChar.getObjectId());
		writeByte(_win ? 1 : 0);
	}
}