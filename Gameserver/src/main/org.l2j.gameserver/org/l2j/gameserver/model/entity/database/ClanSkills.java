package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.l2j.gameserver.model.L2Skill;
import org.springframework.data.annotation.Id;

@Table("clan_skills")
public class ClanSkills extends Entity<Integer> {
    @Id
    @Column("clan_id")
    private int clanId;
    @Column("skill_id")
    private int skillId;
    @Column("skill_level")
    private int skillLevel;
    @Column("skill_name")
    private String skillName;

    public  ClanSkills() {}

    public ClanSkills(int clanId, L2Skill newSkill) {
        this.clanId = clanId;
        this.skillId = newSkill.getId();
        this.skillLevel = newSkill.getLevel();
        this.skillName = newSkill.getName();
    }

    @Override
    public Integer getId() {
        return clanId;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public String getSkillName() {
        return skillName;
    }
}
