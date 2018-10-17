package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: ch Sddddddddd
 * @author KenM
 */
public class ExDuelUpdateUserInfo extends L2GameServerPacket
{
	private static final String _S__FE_4F_EXDUELUPDATEUSERINFO = "[S] FE:4F ExDuelUpdateUserInfo";
	private final L2PcInstance _activeChar;
	
	public ExDuelUpdateUserInfo(L2PcInstance cha)
	{
		_activeChar = cha;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x4f);
		writeString(_activeChar.getName());
		writeInt(_activeChar.getObjectId());
		writeInt(_activeChar.getPlayerClass().getId());
		writeInt(_activeChar.getLevel());
		writeInt((int) _activeChar.getCurrentHp());
		writeInt(_activeChar.getMaxHp());
		writeInt((int) _activeChar.getCurrentMp());
		writeInt(_activeChar.getMaxMp());
		writeInt((int) _activeChar.getCurrentCp());
		writeInt(_activeChar.getMaxCp());
	}

}
