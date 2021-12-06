package com.example.localhub;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiService {
    @GET("/id/")
    Call<LocationId> getLocationId(@Query("localitate") String city, @Query("judet") String state);

}
