package com.example.localhub.entities;

import com.google.gson.annotations.SerializedName;

public class LocationResult {

    @SerializedName("response")
    Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
