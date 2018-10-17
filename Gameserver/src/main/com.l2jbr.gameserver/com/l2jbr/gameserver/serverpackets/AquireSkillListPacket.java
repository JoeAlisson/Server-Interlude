package com.l2jbr.gameserver.serverpackets;

import java.util.LinkedList;
import java.util.List;


/**
 * sample a3 05000000 03000000 03000000 06000000 3c000000 00000000 power strike 10000000 02000000 06000000 3c000000 00000000 mortal blow 38000000 04000000 06000000 36010000 00000000 power shot 4d000000 01000000 01000000 98030000 01000000 ATTACK aura 920sp 8e000000 03000000 03000000 cc010000 00000000
 * Armor Mastery format d (ddddd) skillid, level, maxlevel?, C4 format changes: 0000: [8a] [00 00 00 00] [35 00 00 00] 92 00 00 00 01 00 00 .....5.......... ^^^^^^^^^^^^^ 0010: 00 2d 00 00 00 04 01 00 00 00 00 00 00 a4 00 00 .-.............. 0020: 00 01 00 00 00 03 00 00 00 e4 0c 00 00 00 00 00
 * ................ 0030: 00 d4 00 00 00 01 00 00 00 06 00 00 00 08 52 00 ..............R.
 *
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $
 */
public class AquireSkillListPacket extends L2GameServerPacket {

    public enum skillType {
        Usual,
        Fishing,
        Clan
    }

    private final List<Skill> _skills;
    private final skillType _fishingSkills;

    private class Skill {
        public int id;
        int nextLevel;
        int maxLevel;
        int spCost;
        public int requirements;

        public Skill(int pId, int pNextLevel, int pMaxLevel, int pSpCost, int pRequirements) {
            id = pId;
            nextLevel = pNextLevel;
            maxLevel = pMaxLevel;
            spCost = pSpCost;
            requirements = pRequirements;
        }
    }

    public AquireSkillListPacket(skillType type) {
        _skills = new LinkedList<>();
        _fishingSkills = type;
    }

    public void addSkill(int id, int nextLevel, int maxLevel, int spCost, int requirements) {
        _skills.add(new Skill(id, nextLevel, maxLevel, spCost, requirements));
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x90);
        writeShort(_skills.size());
        writeInt(_fishingSkills.ordinal()); // c4 : C5 : 0: usuall 1: fishing 2: clans
        writeInt(_skills.size());

        for (Skill temp : _skills) {
            writeInt(temp.id);
            writeInt(temp.nextLevel);
            writeInt(temp.maxLevel);
            writeInt(temp.spCost);
            writeInt(temp.requirements);
        }
    }

    @Override
    protected int packetSize() {
        return _skills.size() * 20 + 13;
    }
}
