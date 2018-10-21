package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("henna_trees")
public class HennaTrees  {

    @Id
    @Column("class_id")
    private int classId;
    @Column("symbol_id")
    private int symbolId;

    public int getClassId() {
        return classId;
    }

    public int getSymbolId() {
        return symbolId;
    }
}
