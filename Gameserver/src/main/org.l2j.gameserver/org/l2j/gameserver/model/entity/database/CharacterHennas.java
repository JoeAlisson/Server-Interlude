package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("character_hennas")
public class CharacterHennas extends Entity<Integer> {

    @Id
    @Column("char_obj_id")
    private int charObjId;
    @Column("symbol_id")
    private int symbolId;
    private int slot;
    @Column("class_index")
    private int classIndex;

    public  CharacterHennas() {}

    public CharacterHennas(int objectId, int symbolId, int classIndex, int slot) {
        this.charObjId = objectId;
        this.symbolId = symbolId;
        this.classIndex = classIndex;
        this.slot = slot;
    }

    @Override
    public Integer getId() {
        return charObjId;
    }

    public int getSymbolId() {
        return symbolId;
    }

    public int getSlot() {
        return slot;
    }

    public int getClassIndex() {
        return classIndex;
    }
}
