package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class CharSelected extends L2GameServerPacket {
	// SdSddddddddddffddddddddddddddddddddddddddddddddddddddddd d
	private static final String _S__21_CHARSELECTED = "[S] 15 CharSelected";
	private final L2PcInstance _activeChar;
	private final int _sessionId;
	
	/**
	 * @param cha
	 * @param sessionId
	 */
	public CharSelected(L2PcInstance cha, int sessionId)
	{
		_activeChar = cha;
		_sessionId = sessionId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x15);
		
		writeString(_activeChar.getName());
		writeInt(_activeChar.getCharId()); // ??
		writeString(_activeChar.getTitle());
		writeInt(_sessionId);
		writeInt(_activeChar.getClanId());
		writeInt(0x00); // ??
		writeInt(_activeChar.getSex());
		writeInt(_activeChar.getRace().ordinal());
		writeInt(_activeChar.getPlayerClass().getId());
		writeInt(0x01); // active ??
		writeInt(_activeChar.getX());
		writeInt(_activeChar.getY());
		writeInt(_activeChar.getZ());
		
		writeDouble(_activeChar.getCurrentHp());
		writeDouble(_activeChar.getCurrentMp());
		writeInt((int)_activeChar.getSp());
		writeLong(_activeChar.getExp());
		writeInt(_activeChar.getLevel());
		writeInt(_activeChar.getKarma()); // thx evill33t
		writeInt(0x0); // ?
		writeInt(_activeChar.getINT());
		writeInt(_activeChar.getSTR());
		writeInt(_activeChar.getCON());
		writeInt(_activeChar.getMEN());
		writeInt(_activeChar.getDEX());
		writeInt(_activeChar.getWIT());
		for (int i = 0; i < 30; i++) {
			writeInt(0x00);
		}
		// writeInt(0); //c3
		// writeInt(0); //c3
		// writeInt(0); //c3
		
		writeInt(0x00); // c3 work
		writeInt(0x00); // c3 work
		
		// extra info
		writeInt(GameTimeController.getInstance().getGameTime()); // in-game time
		
		writeInt(0x00); //
		
		writeInt(0x00); // c3
		
		writeInt(0x00); // c3 InspectorBin
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		
		writeInt(0x00); // c3 InspectorBin for 528 client
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		writeInt(0x00); // c3
		writeInt(0x00); // c3
	}

}
