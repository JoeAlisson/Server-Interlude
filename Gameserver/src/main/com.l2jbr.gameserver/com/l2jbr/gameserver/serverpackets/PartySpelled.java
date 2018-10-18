package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Character;
import com.l2jbr.gameserver.model.actor.instance.L2PetInstance;
import com.l2jbr.gameserver.model.actor.instance.L2SummonInstance;

import java.util.LinkedList;
import java.util.List;

public class PartySpelled extends L2GameServerPacket {

    private final List<Effect> _effects;
    private final L2Character _activeChar;

    private class Effect {
        protected int _skillId;
        protected int _dat;
        protected int _duration;

        public Effect(int pSkillId, int pDat, int pDuration) {
            _skillId = pSkillId;
            _dat = pDat;
            _duration = pDuration;
        }
    }

    public PartySpelled(L2Character cha) {
        _effects = new LinkedList<>();
        _activeChar = cha;
    }

    @Override
    protected final void writeImpl() {
        if (_activeChar == null) {
            return;
        }
        writeByte(0xee);
        writeInt(_activeChar instanceof L2SummonInstance ? 2 : _activeChar instanceof L2PetInstance ? 1 : 0);
        writeInt(_activeChar.getObjectId());
        writeInt(_effects.size());
        for (Effect temp : _effects) {
            writeInt(temp._skillId);
            writeShort(temp._dat);
            writeInt(temp._duration / 1000);
        }

    }

    public void addPartySpelledEffect(int skillId, int dat, int duration) {
        _effects.add(new Effect(skillId, dat, duration));
    }
}
