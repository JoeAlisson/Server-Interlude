package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.instancemanager.CastleManager;
import com.l2jbr.gameserver.model.L2Attackable;
import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.L2SiegeClan;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.Castle;

public class Die extends L2GameServerPacket {

	private final int _charObjId;
	private final boolean _fake;
	private boolean _sweepable;
	private int _access;
	private com.l2jbr.gameserver.model.L2Clan _clan;
	private static final int REQUIRED_LEVEL = Config.GM_FIXED;
	L2Character _activeChar;

	public Die(L2Character cha)
	{
		_activeChar = cha;
		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;
			_access = player.getAccessLevel();
			_clan = player.getClan();
			
		}
		_charObjId = cha.getObjectId();
		_fake = !cha.isDead();
		if (cha instanceof L2Attackable)
		{
			_sweepable = ((L2Attackable) cha).isSweepActive();
		}
		
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_fake)
		{
			return;
		}
		
		writeByte(0x06);
		
		writeInt(_charObjId);
		// NOTE:
		// 6d 00 00 00 00 - to nearest village
		// 6d 01 00 00 00 - to hide away
		// 6d 02 00 00 00 - to castle
		// 6d 03 00 00 00 - to siege HQ
		// sweepable
		// 6d 04 00 00 00 - FIXED
		
		writeInt(0x01); // 6d 00 00 00 00 - to nearest village
		if (_clan != null)
		{
			L2SiegeClan siegeClan = null;
			Boolean isInDefense = false;
			Castle castle = CastleManager.getInstance().getCastle(_activeChar);
			if ((castle != null) && castle.getSiege().getIsInProgress())
			{
				// siege in progress
				siegeClan = castle.getSiege().getAttackerClan(_clan);
				if ((siegeClan == null) && castle.getSiege().checkIsDefender(_clan))
				{
					isInDefense = true;
				}
			}
			
			writeInt(_clan.getHasHideout() > 0 ? 0x01 : 0x00); // 6d 01 00 00 00 - to hide away
			writeInt((_clan.getCastle() > 0) || isInDefense ? 0x01 : 0x00); // 6d 02 00 00 00 - to castle
			writeInt((siegeClan != null) && !isInDefense && (siegeClan.getFlag().size() > 0) ? 0x01 : 0x00); // 6d 03 00 00 00 - to siege HQ
		}
		else
		{
			writeInt(0x00); // 6d 01 00 00 00 - to hide away
			writeInt(0x00); // 6d 02 00 00 00 - to castle
			writeInt(0x00); // 6d 03 00 00 00 - to siege HQ
		}
		
		writeInt(_sweepable ? 0x01 : 0x00); // sweepable (blue glow)
		writeInt(_access >= REQUIRED_LEVEL ? 0x01 : 0x00); // 6d 04 00 00 00 - to FIXED
	}
}
