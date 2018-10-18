package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class RecipeShopMsg extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	
	public RecipeShopMsg(L2PcInstance player)
	{
		_activeChar = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xdb);
		writeInt(_activeChar.getObjectId());
		writeString(_activeChar.getCreateList().getStoreName());// _activeChar.getTradeList().getSellStoreName());
	}
}
