package com.raycast.domain;

import com.raycast.domain.base.AbstractEntity;

import java.util.Date;

/**
 * Created by Lucas on 29/08/2014.
 */
public class Comment extends AbstractEntity {
    User to;
    User author;
    String comment;
    Date time;

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
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
