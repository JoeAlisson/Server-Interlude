package org.l2j.gameserver.network.serverpackets;

import java.util.LinkedList;
import java.util.List;

/**
 * MagicEffectIcons format h (dhd)
 *
 * @version $Revision: 1.3.2.1.2.6 $ $Date: 2005/04/05 19:41:08 $
 */
public class MagicEffectIcons extends L2GameServerPacket {
    private static final String _S__97_MAGICEFFECTICONS = "[S] 7f MagicEffectIcons";
    private final List<Effect> _effects;

    private class Effect {
        protected int _skillId;
        protected int _level;
        int _duration;

        public Effect(int pSkillId, int pLevel, int pDuration) {
            _skillId = pSkillId;
            _level = pLevel;
            _duration = pDuration;
        }
    }

    public MagicEffectIcons() {
        _effects = new LinkedList<>();
    }

    public void addEffect(int skillId, int level, int duration) {
        _effects.add(new Effect(skillId, level, duration));
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x7f);

        writeShort(_effects.size());

        for (Effect temp : _effects) {
            writeInt(temp._skillId);
            writeShort(temp._level);

            if (temp._duration == -1) {
                writeInt(-1);
            } else {
                writeInt(temp._duration / 1000);
            }
        }
    }
}
