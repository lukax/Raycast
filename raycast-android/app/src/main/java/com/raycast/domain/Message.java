package com.raycast.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raycast.domain.base.AbstractEntity;
import com.raycast.domain.util.CustomLocation;

import java.util.Date;

/**
 * Created by Lucas on 29/08/2014.
 */
public class Message extends AbstractEntity {
    private User author;
    private String message;
    private Date time;
    @JsonProperty("loc")
    private CustomLocation location;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }
}
