package com.way.mat.opentokchat.rest.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.way.mat.opentokchat.rest.responses.BaseResponse;

/**
 * Created by matviy on 19.09.16.
 */
public class Room extends BaseResponse {

    @Expose
    String name;
    @Expose
    String description;
    @Expose
    String sessionId;
    @Expose
    String imageUrl;
    @Expose
    int numberOfSubscribers;

    public Room(String name, String description, String sessionId, String imageUrl, int numberOfSubscribers) {
        this.name = name;
        this.description = description;
        this.sessionId = sessionId;
        this.imageUrl = imageUrl;
        this.numberOfSubscribers = numberOfSubscribers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumberOfSubscribers() {
        return numberOfSubscribers;
    }

    public void setNumberOfSubscribers(int numberOfSubscribers) {
        this.numberOfSubscribers = numberOfSubscribers;
    }

    public static Room create(String json) {
        return new Gson().fromJson(json, Room.class);
    }

    public String serialize() {
        return new Gson().toJson(this);
    }
}
