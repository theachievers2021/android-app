package com.example.localhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.entities.LocationId;
import com.example.localhub.entities.LocationInfo;
import com.example.localhub.entities.LocationResult;
import com.example.localhub.repos.RetrofitApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondScreen extends AppCompatActivity {

    private final String tag = "TEST";
    private Intent intent;
    private Retrofit retrofit;
    private RetrofitApiService service;
    private String locationIdString = null;
    private AdapterLocation myAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);


        //retrofit and service initialization
        retrofit = new Retrofit.Builder()
                .baseUrl("https://orase.peviitor.ro")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitApiService.class);


        //Get the location from the previous activity
        intent = getIntent();
        List<String> location = (List<String>) intent.getSerializableExtra("locationCityAndCounty");
        String idSearchResult = intent.getStringExtra("locationId");



        if(idSearchResult != null){
            Log.v(tag, idSearchResult);
            idSearchResult = idSearchResult.replace("\"","");
           getLocationInfo(idSearchResult);
        }else{
            String county = location.get(1);
            location.set(1,county.substring(0, county.indexOf(' ')));
            getLocationId(location.get(0), location.get(1));
        }

    }

    private void getLocationId(String city, String county){
        //get the location id
        Call<LocationId> call = service.getLocationId(city, county);
        // Call<LocationId> call = service.getLocationId("Zimbor", "Salaj");
        call.enqueue(new Callback<LocationId>() {
            @Override
            public void onResponse(Call<LocationId> call, Response<LocationId> response) {
                LocationId locationId = response.body();
                locationIdString =  locationId.getId();
                getLocationInfo(locationIdString);
            }

            @Override
            public void onFailure(Call<LocationId> call, Throwable t) {
                locationIdString =  "ERROR";
                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocationInfo(String id) {

        //encode the location string
        id = id.replace(" ","%20");
        String locationid = "%22" + id + "%22";

        Call<LocationResult> call = service.getLocationInfo(locationid);
        Log.v(tag, String.valueOf(call.request()));
        call.enqueue(new Callback<LocationResult>() {
            @Override
            public void onResponse(Call<LocationResult> call, Response<LocationResult> response) {
                LocationResult locationResult = response.body();
                if(locationResult.equals(null)){
                    Log.v(tag, "SOMETHING WENT WRONG");
                }
                createRecyclerView(locationResult.getResponse().getLocationInfoList());
            }

            @Override
            public void onFailure(Call<LocationResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRecyclerView(List<LocationInfo> locationInfoList){
        recyclerView = findViewById(R.id.recyclerViewLocation);
        myAdapter = new AdapterLocation(SecondScreen.this, locationInfoList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 1, GridLayoutManager.VERTICAL, false));
    }
}