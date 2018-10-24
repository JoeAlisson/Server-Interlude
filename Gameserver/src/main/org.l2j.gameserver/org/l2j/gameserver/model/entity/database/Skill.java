package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("skill_trees")
public class Skill implements SkillInfo {

    @Id
    @Column("class_id")
    private int classId;
    @Column("skill_id")
    private int id;
    private int level;
    private String name;
    private int sp;
    @Column("min_level")
    private int minLevel;

    public int getClassId() {
        return classId;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int getSpCost() {
        return sp;
    }

    @Override
    public int getCostId() {
        return 0;
    }

    @Override
    public int getCostCount() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public int getMinLevel() {
        return minLevel;
    }
}
