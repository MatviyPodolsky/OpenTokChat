package com.way.mat.opentokchat.rest.requests;

import com.google.gson.annotations.Expose;

/**
 * Created by matviy on 05.10.16.
 */
public class CreateRoomRequest {

    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String imageUrl;

    public CreateRoomRequest(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
