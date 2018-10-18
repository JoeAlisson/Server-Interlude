package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

/**
 * sample b0 d8 a8 10 48 objectId 00 00 00 00 00 00 00 00 00 00 format ddddS
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartyMatchDetail extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;

	public PartyMatchDetail(L2PcInstance player)
	{
		_activeChar = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x97);
		
		writeInt(_activeChar.getObjectId());
		if (_activeChar.isPartyMatchingShowLevel())
		{
			writeInt(1); // show level
		}
		else
		{
			writeInt(0); // hide level
		}
		
		if (_activeChar.isPartyMatchingShowClass())
		{
			writeInt(1); // show class
		}
		else
		{
			writeInt(0); // hide class
		}
		
		writeInt(0); // c2
		
		writeString("  " + _activeChar.getPartyMatchingMemo()); // seems to be bugged.. first 2 chars get stripped away
	}
}
