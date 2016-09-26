package com.way.mat.opentokchat.rest.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.way.mat.opentokchat.rest.service.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alexander on 29.02.2016.
 */

public class RestClient {

    public static final String SERVER_URL = "http:/192.168.1.122:3000/";

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static final OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        return builder.build();
    }

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private static final ApiService apiService = retrofit.create(ApiService.class);

    public static ApiService getApiService() {
        return apiService;
    }

}
