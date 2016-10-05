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

    public static final String SERVER_URL = "http://192.168.15.15:3000/";

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

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    private static final ApiService apiService = retrofit.create(ApiService.class);

    public static ApiService getApiService() {
        return apiService;
    }

    public static ApiService getApiServiceNew() {
        return createService(ApiService.class);
    }

}
