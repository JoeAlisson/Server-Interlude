package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class HennaInfoPacket extends L2GameServerPacket
{
	private final L2PcInstance _player;

	public HennaInfoPacket(L2PcInstance player)
	{
		_player = player;
	}

	@Override
	protected final void writeImpl() {
		writeByte(0xE5);
		writeShort(_player.getHennaStatINT()); //equip INT
		writeShort(_player.getHennaStatSTR()); //equip STR
		writeShort(_player.getHennaStatCON()); //equip CON
		writeShort(_player.getHennaStatMEN()); //equip MEN
		writeShort(_player.getHennaStatDEX()); //equip DEX
		writeShort(_player.getHennaStatWIT()); //equip WIT
		writeShort(0x00); //equip LUC
		writeShort(0x00); //equip CHA
		writeInt(3); //interlude, slots?
		writeInt(0);
	/*	for(Henna henna : _hennaList.values(false))
		{
			writeInt(henna.getPlayerTemplate().getSymbolId());
			writeInt(_hennaList.isActive(henna));
		}

		Henna henna = _hennaList.getPremiumHenna();
		if(henna != null)
		{
			writeInt(henna.getPlayerTemplate().getSymbolId());	// Premium symbol ID
			writeInt(henna.getLeftTime());	// Premium symbol left time
			writeInt(_hennaList.isActive(henna));	// Premium symbol active
		}
		else
		{ */
			writeInt(0x00);	// Premium symbol ID
			writeInt(0x00);	// Premium symbol left time
			writeInt(0x00);	// Premium symbol active
		//}
	}
}