package com.raycast.service.auth;

import android.content.Context;
import android.util.Log;

import com.raycast.domain.auth.Token;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Lucas on 08/10/2014.
 */
@EBean(scope = EBean.Scope.Singleton)
public class RaycastAuthStore {
    private static final String TOKEN_FILE_NAME = "RAYCAST_TOKEN";
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
            token = getFromFile(TOKEN_FILE_NAME);
        }
        return token;
    }
    public void setToken(Token token) {
        this.token = token;
        saveToFile(token, TOKEN_FILE_NAME);
    }

    private void saveToFile(Token token, String fileName){
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(token);
            os.close();
        } catch (Exception e) {
            Log.e("Token", "unable to save token to file", e);
        }
    }

    private Token getFromFile(String fileName){
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Token t = (Token) is.readObject();
            is.close();
            return t;
        } catch (Exception e) {
            Log.e("Token", "unable to retrieve token from file", e);
        }
        return null;
    }
}
