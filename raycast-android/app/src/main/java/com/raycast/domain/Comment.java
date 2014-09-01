package com.raycast.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raycast.domain.base.AbstractEntity;

import java.util.Date;

/**
 * Created by Lucas on 29/08/2014.
 */
public class Comment extends AbstractEntity {
    @JsonProperty("message_id")
    private long messageId;
    private User author;
    private String comment;
    private Date time;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
