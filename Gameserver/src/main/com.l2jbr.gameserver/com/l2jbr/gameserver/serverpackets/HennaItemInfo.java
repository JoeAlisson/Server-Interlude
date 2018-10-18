package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.database.Henna;


public class HennaItemInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final Henna _henna;
	
	public HennaItemInfo(Henna henna, L2PcInstance player)
	{
		_henna = henna;
		_activeChar = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		
		writeByte(0xe3);
		writeInt(_henna.getSymbolId()); // symbol Id
		writeInt(_henna.getDyeId()); // item id of dye
		writeInt(_henna.getDyeAmount()); // total amount of dye require
		writeInt(_henna.getPrice()); // total amount of aden require to draw symbol
		writeInt(1); // able to draw or not 0 is false and 1 is true
		writeInt(_activeChar.getAdena());
		
		writeInt(_activeChar.getINT()); // current INT
		writeByte(_activeChar.getINT() + _henna.getStatINT()); // equip INT
		writeInt(_activeChar.getSTR()); // current STR
		writeByte(_activeChar.getSTR() + _henna.getStatSTR()); // equip STR
		writeInt(_activeChar.getCON()); // current CON
		writeByte(_activeChar.getCON() + _henna.getStatCON()); // equip CON
		writeInt(_activeChar.getMEN()); // current MEM
		writeByte(_activeChar.getMEN() + _henna.getStatMEM()); // equip MEM
		writeInt(_activeChar.getDEX()); // current DEX
		writeByte(_activeChar.getDEX() + _henna.getStatDEX()); // equip DEX
		writeInt(_activeChar.getWIT()); // current WIT
		writeByte(_activeChar.getWIT() + _henna.getStatWIT()); // equip WIT
	}
}
