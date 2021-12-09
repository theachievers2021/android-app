/*
package com.example.localhub;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.entities.LocationInfo;
import com.example.localhub.repos.RetrofitApiService;
import com.example.localhub.services.AdapterLocation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
*/
/*
public class ListServicesScreen extends AppCompatActivity {

    private final String tag = "TEST";
    private Intent intent;
    private Retrofit retrofit;
    private RetrofitApiService service;
    private String locationIdString = null;
    private AdapterLocation myAdapter;
    RecyclerView recyclerView;
    TextView currentLocation;
    SearchView searchView;
   // EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);
        currentLocation = findViewById(R.id.currentLocation);
        searchView = findViewById(R.id.searchView);
        AutoCompleteTextView searchEditText = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);

        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

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
            currentLocation.setText(idSearchResult);
           getLocationInfo(idSearchResult);
        }else{
            String county = location.get(1);
            location.set(1,county.substring(0, county.indexOf(' ')));
            //Toast.makeText(getApplicationContext(), location.get(0) + "," + location.get(1), Toast.LENGTH_SHORT).show();
            getLocationId(location.get(0), location.get(1));
        }

    }

    private void getLocationId(String city, String county){
        //get the location id
        Call<LocationId> call = service.getLocationId(city, county);
        // Call<LocationId> call = service.getLocationId("Zimbor", "Salaj");
       // Log.v(tag, call.request().toString());
        call.enqueue(new Callback<LocationId>() {
            @Override
            public void onResponse(Call<LocationId> call, Response<LocationId> response) {
                LocationId locationId = response.body();
                locationIdString =  locationId.getId();
               // Toast.makeText(getApplicationContext(), locationIdString, Toast.LENGTH_SHORT).show();
                currentLocation.setText(locationIdString);
                getLocationInfo(locationIdString);
            }

            @Override
            public void onFailure(Call<LocationId> call, Throwable t) {
                locationIdString =  "ERROR";
                Toast.makeText(getApplicationContext(), "An error occurred trying to get the location ID...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getLocationInfo(String id) {

        //encode the location string
        id = id.replace(" ","%20");
        String locationid = "%22" + id + "%22";

        Call<LocationResult> call = service.getLocationInfo(locationid);
        Log.v(tag, call.request().toString());
        call.enqueue(new Callback<LocationResult>() {
            @Override
            public void onResponse(Call<LocationResult> call, Response<LocationResult> response) {
                LocationResult locationResult = response.body();
                if(locationResult.equals(null)){
                    Log.v(tag, "SOMETHING WENT WRONG");
                }
                //Toast.makeText(getApplicationContext(), , Toast.LENGTH_SHORT).show();
                createRecyclerView(locationResult.getResponse().getLocationInfoList());
            }

            @Override
            public void onFailure(Call<LocationResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred trying to fetch the services...", Toast.LENGTH_SHORT).show();
                Log.v(tag, t.getMessage());
            }
        });
    }


    private void createRecyclerView(List<LocationInfo> locationInfoList){
        recyclerView = findViewById(R.id.recyclerViewLocation);
        myAdapter = new AdapterLocation(ListServicesScreen.this, locationInfoList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 1, GridLayoutManager.VERTICAL, false));
    }
} *//*



public class ListServicesScreen extends AppCompatActivity {

    private TextView services_list,location;
    private Intent intent;
    private Retrofit retrofit;
    private RetrofitApiService service;
    private BottomNavigationView bottomNavigationView;
    private CurrentLocation currentLocation;

    private final String tag = "TEST";
    private String locationIdString = null;
    private AdapterLocation myAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_screen);
        location=findViewById(R.id.location);
        bottomNavigationView=findViewById(R.id.bottom_navigation);



        currentLocation = new CurrentLocation(this);
        bottomNavigationView.setSelectedItemId(R.id.current_place);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                   */
/*     startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                        overridePendingTransition(0,0);*//*


                        finish();
                        return true;
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(),FavoritesScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.current_place:
                        currentLocation.current_location();
                        finish();
                        if(currentLocation.isLocationFound()){
                            return true;
                        }
                        else
                            return false;
                    case R.id.add:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_localhub)));
                        startActivity(intent);
                        return false;

                }
                return false;

            }
        });

        intent = getIntent();
        String locationStr = intent.getStringExtra("locationCityAndCounty");
        location.setText(locationStr);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://orase.peviitor.ro")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitApiService.class);

        //Toast.makeText(getApplicationContext(), locationStr, Toast.LENGTH_SHORT).show();
        getLocationInfo(locationStr);

    }

    private void getLocationInfo(String id) {

        Call<JsonObject> call = service.getLocationInfo("\""+id+"\"");
        Log.v(tag, String.valueOf(call.request()));
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject data = response.body();
                if(data.equals(null)){
                    Log.v("HUB", "SOMETHING WENT WRONG");
                }
                JsonObject responseData = (JsonObject) data.get("response");


                List<LocationInfo> serviceList = new ArrayList<>();
                Gson gson = new Gson();

                JsonArray responseArray = responseData.getAsJsonArray("docs");
                if( responseArray == null || responseArray.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Nu exista servicii de afisat", Toast.LENGTH_SHORT).show();
                }else{
                    for (JsonElement jsonElement : responseData.getAsJsonArray("docs")
                         ) {
                        LocationInfo service = gson.fromJson(jsonElement.toString(), LocationInfo.class);
                        serviceList.add(service);
                    }
                    createRecyclerView(serviceList);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRecyclerView(List<LocationInfo> locationInfoList){
        recyclerView = findViewById(R.id.recyclerViewLocation);
        myAdapter = new AdapterLocation(ListServicesScreen.this, locationInfoList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 1, GridLayoutManager.VERTICAL, false));
    }

}*/
