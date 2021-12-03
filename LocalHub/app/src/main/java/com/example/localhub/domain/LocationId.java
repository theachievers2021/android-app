package com.example.localhub.domain;

import com.google.gson.annotations.SerializedName;

public class LocationId {

    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
