package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch S
 * @author KenM
 */
public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	private final String _charName;
	
	public ExAskJoinPartyRoom(String charName)
	{
		_charName = charName;
	}
	
	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x34);
		writeString(_charName);
	}
}
