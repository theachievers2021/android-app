package com.example.localhub.entities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.localhub.R;
import com.example.localhub.pages.ServiceListFragment;
import com.example.localhub.repos.LocationRepository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentLocation implements OnMapReadyCallback, LocationListener {
    private LocationManager locationManager;
    private String locationCity = "";
    private LatLng latLngCity;
    private Activity activity;
    private boolean locationFound=false;
    private String lastLocation = null;
    private ServiceListFragment serviceListFragment;
    private FragmentManager fragmentManager;

    public CurrentLocation(Activity activity, ServiceListFragment serviceListFragment, FragmentManager fragmentManager) {
        this.activity = activity;
        this.serviceListFragment = serviceListFragment;
        this.fragmentManager = fragmentManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public LatLng getLatLngCity() {
        return latLngCity;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean isLocationFound() {
        return locationFound;
    }

    public void current_location() {
        grantPermission();
        checkLocationIsEnabled();
        getLocation();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
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
            activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

    }


    private void grantPermission(){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},100);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder=new Geocoder(activity.getApplicationContext(), Locale.getDefault());
        try{
            List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            locationCity=addressList.get(0).getLocality();
            latLngCity=new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("HUB",locationCity);

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl("https://orase.peviitor.ro/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            LocationRepository locationApi = retrofit.create(LocationRepository.class);
            Call<JsonObject> call = locationApi.getLocation(locationCity);
            Log.v("MOR", call.request().toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(activity.getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                        System.out.println("Eroare");
                        return;
                    } else {
                        JsonObject data = response.body();

                        JsonObject responseData = (JsonObject) data.get("response");
                        int numLocations = responseData.getAsJsonArray("docs").size();
                        if(numLocations==0){
                            Toast.makeText(activity,"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();
                            locationFound=false;
                        }else {
                            locationFound=true;
                            List<String> allIds = new ArrayList<>();
                            String id = "";

                            for(int i= 0;i < numLocations;i++) {
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String[] ids = String.valueOf(locationData.get("id")).split("\"");
                                if (!allIds.contains(ids[1])) {
                                    allIds.add(ids[1]);
                                }
                                if(ids[1].contains(locationCity)){
                                    id = ids[1];
                                    break;
                                }

                            }
                            lastLocation = id;

                            Bundle args = new Bundle();
                            args.putString("locationCityAndCounty", id);
                            serviceListFragment.setArguments(args);
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment_container, serviceListFragment);
                            transaction.disallowAddToBackStack();
                            transaction.commit();
                        }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(activity.getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
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

    public String getLastLocation() {
        return lastLocation;
    }
}
