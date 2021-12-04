package com.example.localhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localhub.repos.LocationRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{

    private TextView latField;
    private TextView longField;
    private TextView addressField;
    private FloatingActionButton reqBtn;
    private LocationManager locationManager;

    private Location lastKnownLocation;



    TextInputEditText textCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        reqBtn = findViewById(R.id.reqBtn);


        requestLocation();
        //checkPermissions();

        configure_button();
        configure_inputText();
    }

    private void requestLocation(){
        //if the permission is granted, get location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lastKnownLocation = location;
                }
            });
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } else {
            //request permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
          /*  while(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //requestLocation();
            }*/

        }
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            //requestLocation();

            return false;
        }
        return true;
    }

    private void configure_button() {

        // this code won't execute IF permissions are not allowed
        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermissions()) {
                    requestLocation();

                    //noinspection MissingPermission
                    List<String> location = getLocationName(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                    Intent intent = new Intent(getApplicationContext(), ServicesScreen.class);
                    intent.putExtra("locationCityAndCounty", (Serializable) location);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Location is required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    //Get the city and state of the surrent location
    public List<String> getLocationName(double latitude, double longitude) {
        String cityName = "Not Found";
        String stateName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adrs : addresses) {
                if (adrs != null) {
                    String city = adrs.getLocality();
                    String state = adrs.getAdminArea();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        System.out.println("city ::  " + cityName);
                    } else {
                        if (state != null && !state.equals("")) {
                            stateName = state;
                            System.out.println("city ::  " + stateName);
                        } else {

                        }
                    }
                    // // you should also try with addresses.get(0).toSring();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(Arrays.asList(cityName, stateName));
    }


    private void configure_inputText(){
        textCity=findViewById(R.id.text_city);

        List<String>allIds=new ArrayList<>();


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://zimbor.go.ro/solr/romania/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LocationRepository locationApi=retrofit.create(LocationRepository.class);

        textCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                allIds.clear();
                Call<JsonObject> call=locationApi.getLocation(textView.getText().toString(),true,"AND","*:*");
                Log.v("MAIN", call.request().toString());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            JsonObject data = response.body();

                            JsonObject responseData = (JsonObject) data.get("response");
                            int numLocations = responseData.getAsJsonArray("docs").size();
                            for(int i=0;i<numLocations;i++){
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String id=String.valueOf(locationData.get("id"));
                                allIds.add(id);

                            }

                            AdapterSearchResult adapterSearchResult = new AdapterSearchResult(MainActivity.this, allIds);
                            RecyclerView recyclerView = findViewById(R.id.text_show);
                            recyclerView.setAdapter(adapterSearchResult);
                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false));

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        });
    }

}