package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Henna;


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
		writeLong(_activeChar.getAdena());
		
		writeInt(_activeChar.getIntelligence()); // current INT
		writeByte(_activeChar.getIntelligence() + _henna.getStatINT()); // equip INT
		writeInt(_activeChar.getStrength()); // current STR
		writeByte(_activeChar.getStrength() + _henna.getStatSTR()); // equip STR
		writeInt(_activeChar.getConstitution()); // current CON
		writeByte(_activeChar.getConstitution() + _henna.getStatCON()); // equip CON
		writeInt(_activeChar.getMentality()); // current MEM
		writeByte(_activeChar.getMentality() + _henna.getStatMEM()); // equip MEM
		writeInt(_activeChar.getDexterity()); // current DEX
		writeByte(_activeChar.getDexterity() + _henna.getStatDEX()); // equip DEX
		writeInt(_activeChar.getWisdom()); // current WIT
		writeByte(_activeChar.getWisdom() + _henna.getStatWIT()); // equip WIT
	}
}
