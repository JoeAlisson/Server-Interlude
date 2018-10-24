package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Table;

@Table("auto_chat_text")
public class AutoChatText  {

    private int groupId;
    private String chatText;

    public String getChatText() {
        return chatText;
    }
}
