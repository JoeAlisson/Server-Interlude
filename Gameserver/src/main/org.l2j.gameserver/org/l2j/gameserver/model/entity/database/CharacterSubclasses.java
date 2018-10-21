package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.l2j.gameserver.model.base.SubClass;
import org.springframework.data.annotation.Id;

@Table("character_subclasses")
public class CharacterSubclasses extends Entity<Integer> {

    @Id
    @Column("char_obj_id")
    private int charObjId;
    @Column("class_id")
    private int classId;
    private long exp;
    private int sp;
    private byte level;
    @Column("class_index")
    private int classIndex;

    public CharacterSubclasses() {}

    public CharacterSubclasses(int charObjectId, SubClass newClass) {
        this.charObjId = charObjectId;
        this.classId = newClass.getClassId();
        this.exp = newClass.getExp();
        this.sp = newClass.getSp();
        this.level = newClass.getLevel();
        this.classIndex = newClass.getClassIndex();
    }

    @Override
    public Integer getId() {
        return charObjId;
    }

    public int getClassId() {
        return classId;
    }

    public long getExp() {
        return exp;
    }

    public int getSp() {
        return sp;
    }

    public byte getLevel() {
        return level;
    }

    public int getClassIndex() {
        return classIndex;
    }
}
