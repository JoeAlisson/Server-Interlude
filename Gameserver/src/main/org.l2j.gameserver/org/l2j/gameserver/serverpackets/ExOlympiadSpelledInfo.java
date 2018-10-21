
package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.LinkedList;
import java.util.List;

public class ExOlympiadSpelledInfo extends L2GameServerPacket {

    private final L2PcInstance _player;
    private final List<Effect> _effects;

    private class Effect {
        protected int _skillId;
        int _dat;
        int _duration;

        public Effect(int pSkillId, int pDat, int pDuration) {
            _skillId = pSkillId;
            _dat = pDat;
            _duration = pDuration;
        }
    }

    public ExOlympiadSpelledInfo(L2PcInstance player) {
        _effects = new LinkedList<>();
        _player = player;
    }

    public void addEffect(int skillId, int dat, int duration) {
        _effects.add(new Effect(skillId, dat, duration));
    }

    @Override
    protected final void writeImpl() {
        if (_player == null) {
            return;
        }
        writeByte(0xfe);
        writeShort(0x2a);
        writeInt(_player.getObjectId());
        writeInt(_effects.size());
        for (Effect temp : _effects) {
            writeInt(temp._skillId);
            writeShort(temp._dat);
            writeInt(temp._duration / 1000);
        }
    }
}
