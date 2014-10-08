package com.raycast.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lucas on 08/10/2014.
 */
public abstract class AuthCredential {
    @JsonProperty("client_id") private String clientId;
    @JsonProperty("client_secret") private String clientSecret;
    @JsonProperty("grant_type") private String grantType;

    public AuthCredential(String clientId, String clientSecret, String grantType){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
    }
}
