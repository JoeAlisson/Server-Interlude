package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("fishing_skill_trees")
public class FishingSkill implements SkillInfo {

    @Id
    @Column("skill_id")
    private int id;
    private int level;
    private String name;
    private int sp;
    @Column("min_level")
    private int minLevel;
    private int costId;
    private int cost;
    private int isForDwarf;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getSpCost() { return sp; }

    @Override
    public int getCostId() {  return costId; }

    @Override
    public int getCostCount() { return cost; }

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    public String getName() {
        return name;
    }
}
