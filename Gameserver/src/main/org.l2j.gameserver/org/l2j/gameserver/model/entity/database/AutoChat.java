package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Table("auto_chat")
public class AutoChat  {

    @Id
    private Integer groupId;
    private Integer npcId;
    private Long chatDelay;
    @Column("groupId")
    private Set<AutoChatText> texts;

    public Integer getGroupId() {
        return groupId;
    }

    public Integer getNpcId() {
        return npcId;
    }

    public Long getChatDelay() {
        return chatDelay;
    }

    public Set<AutoChatText> getTexts() {
        return texts;
    }
}
