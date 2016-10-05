package com.way.mat.opentokchat.rest.responses;

import com.google.gson.annotations.Expose;
import com.way.mat.opentokchat.rest.models.Room;

/**
 * Created by matviy on 05.10.16.
 */
public class CreateRoomResponse extends BaseResponse {

    @Expose
    Room response;

    public CreateRoomResponse(Room response) {
        this.response = response;
    }

    public Room getResponse() {
        return response;
    }

    public void setResponse(Room response) {
        this.response = response;
    }
}
