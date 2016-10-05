package com.way.mat.opentokchat.rest.responses;

import com.google.gson.annotations.Expose;
import com.way.mat.opentokchat.rest.models.Room;

import java.util.List;

/**
 * Created by matviy on 05.10.16.
 */
public class RoomsResponse extends BaseResponse {

    @Expose
    List<Room> response;

    public RoomsResponse(List<Room> response) {
        this.response = response;
    }

    public List<Room> getResponse() {
        return response;
    }

    public void setResponse(List<Room> response) {
        this.response = response;
    }
}
