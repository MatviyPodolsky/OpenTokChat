package com.way.mat.opentokchat.rest.responses;

import com.google.gson.annotations.Expose;

/**
 * Created by matviy on 05.10.16.
 */
public class FileUploadResponse extends BaseResponse {

    @Expose
    UploadResponseBody response;

    public FileUploadResponse(UploadResponseBody response) {
        this.response = response;
    }

    public UploadResponseBody getResponse() {
        return response;
    }

    public void setResponse(UploadResponseBody response) {
        this.response = response;
    }
}
