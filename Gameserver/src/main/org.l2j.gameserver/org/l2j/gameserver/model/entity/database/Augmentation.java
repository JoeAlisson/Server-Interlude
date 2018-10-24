package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.l2j.gameserver.model.L2Skill;
import org.springframework.data.annotation.Id;

import static java.util.Objects.nonNull;

@Table("augmentations")
public class Augmentation extends Entity<Integer> {

    @Id
    @Column("item_id")
    private Integer itemId;
    private Integer attributes;
    private Integer skill;
    private Integer level;

    public  Augmentation() {}

    public Augmentation(int objectId, int effectsId, L2Skill skill) {
        this.itemId = objectId;
        this.attributes = effectsId;
        if(nonNull(skill)) {
            this.skill = skill.getId();
            this.level = skill.getLevel();
        }
    }

    @Override
    public Integer getId() {
        return itemId;
    }

    public int getAttributes() {
        return attributes;
    }

    public Integer getSkill() {
        return skill;
    }

    public Integer getLevel() {
        return level;
    }
}
