package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("character_friends")
public class CharacterFriends extends Entity<Integer> {

    @Id
    @Column("char_id")
    private int charId;
    @Column("friend_id")
    private int friendId;
    @Column("friend_name")
    private String friendName;

    public CharacterFriends() {}

    public CharacterFriends(int objectId, int friendId, String name) {
        this.charId = objectId;
        this.friendId = friendId;
        this.friendName = name;
    }

    @Override
    public Integer getId() {
        return charId;
    }

    public int getFriendId() {
        return friendId;
    }

    public String getFriendName() {
        return friendName;
    }
}
