package com.example.localhub.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localhub.R;
import com.example.localhub.domain.LocationResult;
import com.example.localhub.repository.RetrofitApiService;
import com.google.gson.JsonObject;

import java.awt.font.TextAttribute;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListScreen extends AppCompatActivity {

    private TextView services_list,location;
    private Intent intent;
    private Retrofit retrofit;
    private RetrofitApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_screen);
        location=findViewById(R.id.location);
        services_list=findViewById(R.id.services_list);

        intent = getIntent();
        String locationStr = intent.getStringExtra("locationCityAndCounty");
        location.setText(locationStr);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://orase.peviitor.ro")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitApiService.class);

        getLocationInfo(locationStr);
    }

    private void getLocationInfo(String id) {


        Call<JsonObject> call = service.getLocationInfo("\""+id+"\"");
        Log.v("HUB", String.valueOf(call.request()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject data = response.body();
                if(data.equals(null)){
                    Log.v("HUB", "SOMETHING WENT WRONG");
                }
                JsonObject responseData = (JsonObject) data.get("response");
                services_list.setText(responseData.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}