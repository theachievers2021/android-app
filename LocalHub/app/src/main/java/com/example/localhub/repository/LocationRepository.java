package com.example.localhub.repository;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface LocationRepository {

    @GET("select?")
    Call<JsonObject> getLocation(
            @Query("fq") String city,
            @Query("indent") Boolean isTrue,
            @Query("q.op") String or,
            @Query("q") String code);




}
