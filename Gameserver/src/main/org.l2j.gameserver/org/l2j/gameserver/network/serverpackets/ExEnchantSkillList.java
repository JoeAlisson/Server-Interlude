package org.l2j.gameserver.network.serverpackets;

import java.util.LinkedList;
import java.util.List;

public class ExEnchantSkillList extends L2GameServerPacket {
    private final List<Skill> _skills;

    class Skill {
        public int id;
        int nextLevel;
        public int sp;
        public int exp;

        Skill(int pId, int pNextLevel, int pSp, int pExp) {
            id = pId;
            nextLevel = pNextLevel;
            sp = pSp;
            exp = pExp;
        }
    }

    public void addSkill(int id, int level, int sp, int exp) {
        _skills.add(new Skill(id, level, sp, exp));
    }

    public ExEnchantSkillList() {
        _skills = new LinkedList<>();
    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
     */
    @Override
    protected void writeImpl() {
        writeByte(0xfe);
        writeShort(0x17);

        writeInt(_skills.size());
        for (Skill sk : _skills) {
            writeInt(sk.id);
            writeInt(sk.nextLevel);
            writeInt(sk.sp);
            writeLong(sk.exp);
        }

    }

}