package com.raycast.domain.auth;

/**
 * Created by Lucas on 08/10/2014.
 */
public class CodeAuthCredential extends AuthCredential {
    private String code;

    public CodeAuthCredential(String code, String clientId, String clientSecret) {
        super(clientId, clientSecret, "authorization_code");
        this.code = code;
    }
}
