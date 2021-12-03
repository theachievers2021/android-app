package com.example.localhub.repository;

import com.example.localhub.domain.LocationId;
import com.example.localhub.domain.LocationResult;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiService {
    @GET("/id/")
    Call<LocationId> getLocationId(@Query("localitate") String city, @Query("judet") String state);


    @GET("/api/localhub/get_info/")
    Call<JsonObject> getLocationInfo(@Query("id") String id);

}
