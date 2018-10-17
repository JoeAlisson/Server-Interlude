package com.l2jbr.gameserver.serverpackets;


/**
 * Format: ch S
 * @author KenM
 */
public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	private static final String _S__FE_34_EXASKJOINPARTYROOM = "[S] FE:34 ExAskJoinPartyRoom";
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
