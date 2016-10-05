package com.way.mat.opentokchat.rest.responses;

import com.google.gson.annotations.Expose;

/**
 * Created by matviy on 05.10.16.
 */
public class UploadResponseBody {

    @Expose
    String imageUrl;

    public UploadResponseBody(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
