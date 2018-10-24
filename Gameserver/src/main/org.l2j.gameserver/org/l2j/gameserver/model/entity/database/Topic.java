package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("topic")
public class Topic extends Entity<Integer> {

    @Id
    @Column("topic_id")
    private int topicId;
    @Column("topic_forum_id")
    private int topicForumId;
    @Column("topic_name")
    private String topicName;
    @Column("topic_date")
    private long topicDate;
    @Column("topic_ownername")
    private String topicOwnerName;
    @Column("topic_ownerid")
    private int topicOwnerId;
    @Column("topic_type")
    private int topicType;
    @Column("topic_reply")
    private int topicReply;

    public Topic() {}

    public Topic(int id, int forumId, String topicName, long date, String ownerName, int ownerId, int type, int cReply) {
        this.topicId = id;
        this.topicForumId = forumId;
        this.topicName = topicName;
        this.topicDate = date;
        this.topicOwnerName = ownerName;
        this.topicOwnerId = ownerId;
        this.topicType = type;
        this.topicReply = cReply;
    }

    @Override
    public Integer getId() {
        return topicId;
    }

    public int getTopicForumId() {
        return topicForumId;
    }

    public String getTopicName() {
        return topicName;
    }

    public long getTopicDate() {
        return topicDate;
    }

    public String getTopicOwnerName() {
        return topicOwnerName;
    }

    public int getTopicOwnerId() {
        return topicOwnerId;
    }

    public int getTopicType() {
        return topicType;
    }

    public int getTopicReply() {
        return topicReply;
    }
}
