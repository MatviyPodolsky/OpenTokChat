package com.way.mat.opentokchat.rest.responses;

import com.google.gson.annotations.Expose;

/**
 * Created by Alexander on 29.02.2016.
 */

public class BaseResponse {

    @Expose
    private String status;
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return status.equals("OK");
    }
}
