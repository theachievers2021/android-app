package com.example.localhub.repos;

import com.example.localhub.entities.LocationId;
import com.example.localhub.entities.LocationResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiService {
    @GET("/id/")
    Call<LocationId> getLocationId(@Query("localitate") String city, @Query("judet") String state);


    @GET("/api/localhub/get_info/")
    Call<LocationResult> getLocationInfo(@Query(value = "id", encoded = true) String id);

}
