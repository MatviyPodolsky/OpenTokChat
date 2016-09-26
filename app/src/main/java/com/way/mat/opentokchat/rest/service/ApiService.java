package com.way.mat.opentokchat.rest.service;

import com.way.mat.opentokchat.rest.models.Room;
import com.way.mat.opentokchat.rest.models.TokenData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Alexander on 29.02.2016.
 */

public interface ApiService {

    @GET("api/room/all")
    Call<List<Room>> getRooms();

    @GET("api/room/{session_id}")
    Call<TokenData> getToken(@Path("session_id") String session_id);

}
