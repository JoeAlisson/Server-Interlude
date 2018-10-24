package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("character_recommends")
public class CharacterRecommends extends Entity<Integer> {

    @Id
    @Column("char_id")
    private int charId;
    @Column("target_id")
    private int targetId;

    public CharacterRecommends() {}

    public CharacterRecommends(int charId, int targetId) {
        this.charId = charId;
        this.targetId = targetId;
    }

    @Override
    public Integer getId() {
        return charId;
    }

    public int getTargetId() {
        return targetId;
    }
}
