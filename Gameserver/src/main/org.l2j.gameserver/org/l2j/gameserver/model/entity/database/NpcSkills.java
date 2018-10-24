package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("npcskills")
public class NpcSkills {

    @Id
    private int npcid;
    private int skillid;
    private int level;

    public int getNpcid() {
        return npcid;
    }

    public int getSkillid() {
        return skillid;
    }

    public int getLevel() {
        return level;
    }
}

