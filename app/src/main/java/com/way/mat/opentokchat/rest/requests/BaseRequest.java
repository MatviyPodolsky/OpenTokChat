package com.way.mat.opentokchat.rest.requests;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

/**
 * Created by Alexander on 29.02.2016.
 */

public class BaseRequest {

    @Expose
    private String auth;

    public BaseRequest() {
//        String auth = Prefs.getString(PrefKeys.ACCESS_TOKEN, null);
        if (!TextUtils.isEmpty(auth)) {
            this.auth = auth;
        }
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

}
