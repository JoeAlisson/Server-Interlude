package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class PrivateStoreMsgSell extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private String _storeMsg;
	
	public PrivateStoreMsgSell(L2PcInstance player)
	{
		_activeChar = player;
		if (_activeChar.getSellList() != null)
		{
			_storeMsg = _activeChar.getSellList().getTitle();
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x9c);
		writeInt(_activeChar.getObjectId());
		writeString(_storeMsg);
	}
}
