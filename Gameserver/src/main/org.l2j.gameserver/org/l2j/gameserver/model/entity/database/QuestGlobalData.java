package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("quest_global_data")
public class QuestGlobalData  {

    @Id
    @Column("quest_name")
    private String questName;
    private String var;
    private String value;
}
