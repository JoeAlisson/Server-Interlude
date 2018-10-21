package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

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
