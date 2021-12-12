package com.example.localhub.repos;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface LocationRepository {

    @GET("localitate/")
    Call<JsonObject> getLocation(@Query("nume") String locationName);

}
