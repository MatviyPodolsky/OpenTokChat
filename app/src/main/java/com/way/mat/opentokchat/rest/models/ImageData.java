package com.way.mat.opentokchat.rest.models;

import com.google.gson.annotations.Expose;

/**
 * Created by matviy on 19.09.16.
 */
public class ImageData {

    @Expose
    String contentType;
    @Expose
    String data;

    public ImageData(String contentType, String data) {
        this.contentType = contentType;
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
