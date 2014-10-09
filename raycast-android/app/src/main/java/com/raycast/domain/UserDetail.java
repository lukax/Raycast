package com.raycast.domain;

import com.raycast.domain.auth.Token;

/**
 * Created by Lucas on 29/08/2014.
 */
public class UserDetail {
    private String email;
    private String site;
    private String description;
    private Token authInfo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Token getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(Token authInfo) {
        this.authInfo = authInfo;
    }
}
