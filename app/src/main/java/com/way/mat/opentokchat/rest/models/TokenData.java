package com.way.mat.opentokchat.rest.models;

import com.google.gson.annotations.Expose;

/**
 * Created by matviy on 19.09.16.
 */
public class TokenData {

    @Expose
    String token;

    public TokenData(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
