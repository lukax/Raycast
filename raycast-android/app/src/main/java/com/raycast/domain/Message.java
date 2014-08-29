package com.raycast.domain;

import com.raycast.domain.base.AbstractEntity;
import com.raycast.domain.util.Coordinates;

import java.util.Date;

/**
 * Created by Lucas on 29/08/2014.
 */
public class Message extends AbstractEntity {
    private String author;
    private String message;
    private Date time;
    private Coordinates coordinate;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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

    public Coordinates getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinates coordinate) {
        this.coordinate = coordinate;
    }
}
