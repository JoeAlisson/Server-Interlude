package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Effect;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.skills.effects.EffectCharge;

public class EtcStatusUpdate extends L2GameServerPacket  {
	private static final String _S__F3_ETCSTATUSUPDATE = "[S] F3 EtcStatusUpdate";

	private static final int NO_CHAT_FLAG = 1 << 0;
	private static final int DANGER_AREA_FLAG = 1 << 1;
	private static final int CHARM_OF_COURAGE_FLAG = 1 << 2;
	
	private final L2PcInstance _activeChar;
	private final EffectCharge _effect;
	
	public EtcStatusUpdate(L2PcInstance activeChar) {
		_activeChar = activeChar;
		_effect = (EffectCharge) _activeChar.getFirstEffect(L2Effect.EffectType.CHARGE);
	}

	@Override
	protected void writeImpl() {
		byte flags = 0;
		if(_activeChar.getMessageRefusal() || _activeChar.isChatBanned()) {
			flags |= NO_CHAT_FLAG; //skill id 4269, 1 lvl
		}

		// TODO Implement Danger Area
		/*if(_activeChar.isInDangerArea()) {
			_flags |= DANGER_AREA_FLAG; // skill id 4268, 1 lvl
		}*/

		if(_activeChar.getCharmOfCourage()) {
            flags |= CHARM_OF_COURAGE_FLAG; //Charm of Courage, "Prevents experience value decreasing if killed during a siege war".
        }

		writeByte(0xF9); // several icons to a separate line (0 = disabled)
		if (_effect != null) {
			writeByte(_effect.getLevel()); // 1-7 increase force, lvl
		} else {
			writeByte(0x00); // 1-7 increase force, lvl
		}
		writeInt(_activeChar.getWeightPenalty()); // 1-4 weight penalty, lvl (1=50%, 2=66.6%, 3=80%, 4=100%)
		writeByte((int) _activeChar.getWeaponExpertisePenalty());
		writeByte((int) _activeChar.getArmourExpertisePenalty());
		writeByte(_activeChar.getDeathPenaltyBuffLevel());
		writeByte(0x00); // TODO Soul Expasion
		writeByte(flags);
	}

    @Override
    protected int packetSize() {
        return  13;
    }

    @Override
	public String getType() {
		return _S__F3_ETCSTATUSUPDATE;
	}
}
