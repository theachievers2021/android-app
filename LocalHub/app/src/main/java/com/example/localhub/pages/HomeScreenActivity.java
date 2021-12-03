package com.example.localhub.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.example.localhub.R;
import com.example.localhub.repository.LocationRepository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private AutoCompleteTextView insert_place;
    private Button search_place, current_location;
    private LocationManager locationManager;
    private String locationCity = "";
    private LatLng latLngCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getSupportActionBar().hide();


        insert_place = findViewById(R.id.insert_place);
        search_place = findViewById(R.id.search_place);
        current_location = findViewById(R.id.current_location);


        configure_inputText();
        search_place();
        current_location();


    }

    private void current_location() {
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantPermission();
                checkLocationIsEnabled();
                getLocation();



            }
        });


    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gpsEnabled && !networkEnabled) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

    }



    private void grantPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},100);
        }
    }



    private void search_place() {
        search_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeScreenActivity.this,"ok.",Toast.LENGTH_SHORT).show();
                System.out.println("Buton apasat");
                String location = insert_place.getText().toString();
                if(location.equals("")){
                    Toast.makeText(HomeScreenActivity.this,"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

                }
                else{


                    List<String>allIds=new ArrayList<>();


                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl("http://zimbor.go.ro/solr/romania/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    LocationRepository locationApi=retrofit.create(LocationRepository.class);
                    Call<JsonObject> call=locationApi.getLocation(location,true,"OR","*:*");
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                System.out.println("Eroare");
                                return;
                            } else {
                                JsonObject data = response.body();

                                JsonObject responseData = (JsonObject) data.get("response");
                                int numLocations = responseData.getAsJsonArray("docs").size();
                                if(numLocations==0){
                                    Toast.makeText(HomeScreenActivity.this,"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

                                }else {
                                    //Toast.makeText(HomeScreenActivity.this, location, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),ListScreen.class);
                                    intent.putExtra("locationCityAndCounty", location);
                                    startActivity(intent);

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            System.out.println("Something went wrong...");
                        }
                    });
                }
            }
        });
    }


    private void configure_inputText(){
        List<String>allIds=new ArrayList<>();


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://zimbor.go.ro/solr/romania/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LocationRepository locationApi=retrofit.create(LocationRepository.class);



        insert_place.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //allIds.clear();
                Call<JsonObject> call=locationApi.getLocation('\"'+textView.getText().toString()+'\"',true,"OR","*:*");
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                            System.out.println("Eroare");
                            return;
                        } else {
                            JsonObject data = response.body();

                            JsonObject responseData = (JsonObject) data.get("response");
                            int numLocations = responseData.getAsJsonArray("docs").size();
                            for(int i=0;i<numLocations;i++){
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String[] ids=String.valueOf(locationData.get("id")).split("\"");
                                if(!allIds.contains(ids[1])) {
                                    allIds.add(ids[1]);
                                }
                                System.out.println("id: "+ids[1]);

                            }

                            System.out.println(allIds);
                            ArrayAdapter<String> placesAdapter=new ArrayAdapter<>(HomeScreenActivity.this, android.R.layout.simple_list_item_1,allIds);
                            insert_place.setAdapter(placesAdapter);

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        System.out.println("Something went wrong...");
                    }
                });
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            locationCity=addressList.get(0).getLocality();
            latLngCity=new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("HUB",locationCity);
            System.out.println(locationCity);
            Log.d("HUB","Aixi: "+latLngCity);

            //Toast.makeText(HomeScreenActivity.this, locationCity, Toast.LENGTH_SHORT).show();

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl("http://zimbor.go.ro/solr/romania/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            LocationRepository locationApi=retrofit.create(LocationRepository.class);
            Call<JsonObject> call=locationApi.getLocation('\"'+locationCity+'\"',true,"OR","*:*");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                        System.out.println("Eroare");
                        return;
                    } else {
                        JsonObject data = response.body();

                        JsonObject responseData = (JsonObject) data.get("response");
                        int numLocations = responseData.getAsJsonArray("docs").size();
                        if(numLocations==0){
                            Toast.makeText(HomeScreenActivity.this,"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

                        }else {
                            List<String> allIds = new ArrayList<>();

                            for(int i=0;i<numLocations;i++) {
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String[] ids = String.valueOf(locationData.get("id")).split("\"");
                                if (!allIds.contains(ids[1])) {
                                    allIds.add(ids[1]);
                                }
                            }
                                //Toast.makeText(HomeScreenActivity.this, allIds.get(0), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),ListScreen.class);
                                intent.putExtra("locationCityAndCounty", allIds.get(0));
                                startActivity(intent);
                            }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                    System.out.println("Something went wrong...");
                }
            });


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}