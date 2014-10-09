package com.raycast.service.auth;

import com.raycast.domain.auth.Token;

import org.androidannotations.annotations.EBean;

/**
 * Created by Lucas on 08/10/2014.
 */
@EBean(scope = EBean.Scope.Singleton)
public class RaycastAuthStore {
    private static final String CLIENT_ID = "raycast";
    private static final String CLIENT_SECRET = "android";
    private Token token;

    public String getClientId(){
        return CLIENT_ID;
    }
    public String getClientSecret(){
        return CLIENT_SECRET;
    }

    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }
}
