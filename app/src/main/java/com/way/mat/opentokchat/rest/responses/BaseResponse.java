package com.way.mat.opentokchat.rest.responses;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

/**
 * Created by Alexander on 29.02.2016.
 */

public class BaseResponse {

    @Expose
    private String error;


    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return TextUtils.isEmpty(error);
    }

}
