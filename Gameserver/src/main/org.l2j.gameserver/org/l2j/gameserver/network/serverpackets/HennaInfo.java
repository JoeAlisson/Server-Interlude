package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Henna;

public final class HennaInfo extends L2GameServerPacket {

	private final L2PcInstance _activeChar;
	private final Henna[] _hennas = new Henna[3];
	private final int _count;
	
	public HennaInfo(L2PcInstance player)
	{
		_activeChar = player;
		
		int j = 0;
		for (int i = 0; i < 3; i++)
		{
			Henna h = _activeChar.getHenna(i + 1);
			if (h != null)
			{
				_hennas[j++] = h;
			}
		}
		_count = j;
	}
	
	@Override
	protected final void writeImpl()
	{
		
		writeByte(0xe4);
		
		writeByte(_activeChar.getHennaStatINT()); // equip INT
		writeByte(_activeChar.getHennaStatSTR()); // equip STR
		writeByte(_activeChar.getHennaStatCON()); // equip CON
		writeByte(_activeChar.getHennaStatMEN()); // equip MEM
		writeByte(_activeChar.getHennaStatDEX()); // equip DEX
		writeByte(_activeChar.getHennaStatWIT()); // equip WIT
		
		writeInt(3); // slots?
		
		writeInt(_count); // size
		for (int i = 0; i < _count; i++)
		{
			writeInt(_hennas[i].getSymbolId());
			writeInt(_hennas[i].getSymbolId());
		}
	}
}
