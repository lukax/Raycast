package com.raycast.domain;

import com.raycast.domain.base.AbstractEntity;

/**
 * Created by Lucas on 29/08/2014.
 */
public class User extends AbstractEntity {
    private String username;
    private String name;
    private byte[] image;
    private UserDetail detail;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public UserDetail getDetail() {
        return detail;
    }

    public void setDetail(UserDetail detail) {
        this.detail = detail;
    }
}
