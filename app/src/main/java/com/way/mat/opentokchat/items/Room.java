package com.way.mat.opentokchat.items;

/**
 * Created by matviy on 19.09.16.
 */
public class Room {

    String name;
    String description;
    String sessionId;
    String token;

    public Room(String name, String description, String sessionId, String token) {
        this.name = name;
        this.description = description;
        this.sessionId = sessionId;
        this.token = token;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
