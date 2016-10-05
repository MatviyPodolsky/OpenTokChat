package com.way.mat.opentokchat.rest.service;

import com.way.mat.opentokchat.rest.requests.CreateRoomRequest;
import com.way.mat.opentokchat.rest.responses.CreateRoomResponse;
import com.way.mat.opentokchat.rest.responses.FileUploadResponse;
import com.way.mat.opentokchat.rest.responses.GetTokenResponse;
import com.way.mat.opentokchat.rest.responses.RoomsResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Alexander on 29.02.2016.
 */

public interface ApiService {

    @GET("api/room/all")
    Call<RoomsResponse> getRooms();

    @GET("api/room/{session_id}")
    Call<GetTokenResponse> getToken(@Path("session_id") String session_id);

    @POST("/api/room/create")
    Call<CreateRoomResponse> createRoom(@Body CreateRoomRequest request);

    @Multipart
    @POST("api/file/upload")
    Call<FileUploadResponse> upload(@Part("description") RequestBody description,
                                    @Part MultipartBody.Part file);

}
