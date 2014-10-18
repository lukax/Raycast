package com.raycast.service.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.raycast.domain.auth.Token;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by Lucas on 08/10/2014.
 */
@EBean(scope = EBean.Scope.Singleton)
public class RaycastAuthStore {
    private static final String FILE_NAME = "RAYCAST_TOKEN";
    private static final String FILE_ACCESS_TOKEN = "access_token";
    private static final String FILE_REFRESH_TOKEN = "refresh_token";
    private static final String FILE_EXPIRES_IN = "expires_in";
    private static final String FILE_TOKEN_TYPE = "token_type";
    private static final String CLIENT_ID = "raycast";
    private static final String CLIENT_SECRET = "android";
    @RootContext Context context;
    private Token token;

    public String getClientId(){
        return CLIENT_ID;
    }
    public String getClientSecret(){
        return CLIENT_SECRET;
    }

    public Token getToken() {
        if(token == null){
            token = getFromFile();
        }
        return token;
    }
    public void setToken(Token token) {
        this.token = token;
        saveToFile(token);
    }


    private void saveToFile(Token token){
        if(token == null)
            token = new Token();
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        pref.edit()
            .putString(FILE_ACCESS_TOKEN, token.getAccessToken())
            .putString(FILE_REFRESH_TOKEN, token.getRefreshToken())
            .putString(FILE_TOKEN_TYPE, token.getTokenType())
            .putInt(FILE_EXPIRES_IN, token.getExpiresIn())
            .commit();
    }

    private Token getFromFile(){
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Token token = new Token();
        token.setAccessToken(pref.getString(FILE_ACCESS_TOKEN, ""));
        token.setRefreshToken(pref.getString(FILE_REFRESH_TOKEN, ""));
        token.setTokenType(pref.getString(FILE_TOKEN_TYPE, ""));
        token.setExpiresIn(pref.getInt(FILE_EXPIRES_IN, 0));
        return token;
    }
}
