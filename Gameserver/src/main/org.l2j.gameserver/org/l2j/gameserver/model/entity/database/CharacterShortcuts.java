package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("character_shortcuts")
public class CharacterShortcuts extends Entity<Integer> {

    @Id
    @Column("char_obj_id")
    private int charObjId;
    private int slot;
    private int page;
    private int type;
    @Column("shortcut_id")
    private int shortcutId;
    private int level;
    @Column("class_index")
    private int classIndex;

    @Override
    public Integer getId() {
        return charObjId;
    }

    public int getSlot() {
        return slot;
    }

    public int getPage() {
        return page;
    }

    public int getType() {
        return type;
    }

    public int getShortcutId() {
        return shortcutId;
    }

    public int getLevel() {
        return level;
    }

    public int getClassIndex() {
        return classIndex;
    }
}
