package com.way.mat.opentokchat.rest.responses;

import com.google.gson.annotations.Expose;
import com.way.mat.opentokchat.rest.models.TokenData;

/**
 * Created by matviy on 05.10.16.
 */
public class GetTokenResponse extends BaseResponse {

    @Expose
    TokenData response;

    public GetTokenResponse(TokenData response) {
        this.response = response;
    }

    public TokenData getResponse() {
        return response;
    }

    public void setResponse(TokenData response) {
        this.response = response;
    }
}
