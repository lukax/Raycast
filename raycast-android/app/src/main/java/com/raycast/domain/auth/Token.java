package com.raycast.domain.auth;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Lucas on 29/08/2014.
 */
public class Token implements Parcelable, Serializable {
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("refresh_token") private String refreshToken;
    @JsonProperty("expires_in") private int expiresIn;
    @JsonProperty("token_type") private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accessToken);
        dest.writeString(this.refreshToken);
        dest.writeInt(this.expiresIn);
        dest.writeString(this.tokenType);
    }

    public Token() {
    }

    private Token(Parcel in) {
        this.accessToken = in.readString();
        this.refreshToken = in.readString();
        this.expiresIn = in.readInt();
        this.tokenType = in.readString();
    }

    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() {
        public Token createFromParcel(Parcel source) {
            return new Token(source);
        }

        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    public String getAuthorizationHeader(){
        return "Bearer " + getAccessToken();
    }
}
