package com.raycast.domain.auth;

/**
 * Created by Lucas on 08/10/2014.
 */
public class PasswordAuthCredential extends AuthCredential {
    private String username;
    private String password;

    public PasswordAuthCredential(String username, String password, String clientId, String clientSecret) {
        super(clientId, clientSecret, "authorization_code");
        this.username = username;
        this.password = password;
    }
}
