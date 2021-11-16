package com.example.socialhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetCoordinatesActivity extends AppCompatActivity implements LocationListener {
    EditText etPlace;
    Button btSubmit;
    TextView tvAddress;
    LocationManager locationManager;
    String latitude, longitude,locationCity;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_coordinates);

        etPlace=findViewById(R.id.et_place);
        btSubmit=findViewById(R.id.bt_submit);
        //tvAddress=findViewById(R.id.tv_address);
//
//        supportMapFragment=(SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        supportMapFragment.getMapAsync((OnMapReadyCallback) this);
//        client= LocationServices.getFusedLocationProviderClient(this);
        grantPermission();
        checkLocationIsEnabled();
        getLocation();




        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address=etPlace.getText().toString();
                GeoLocation geoLocation=new GeoLocation();
                geoLocation.getAddress(address,getApplicationContext(),new GeoHandler(),supportMapFragment);
            }
        });
    }

    private void getLocation() {
        try{
            locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,5,(LocationListener) this);

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabled() {
        LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled=false;
        boolean networkEnabled=false;

        try{
            gpsEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            networkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!gpsEnabled && !networkEnabled){
            new AlertDialog.Builder(GetCoordinatesActivity.this)
                    .setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(GetCoordinatesActivity.this,"Unable to find location. Search by city.", Toast.LENGTH_SHORT).show();
                }
            })
                    .show();
        }

    }

    private void grantPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},100);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            locationCity=addressList.get(0).getLocality();
            Log.d("HUB",locationCity);
            GeoLocation geoLocation=new GeoLocation();
            geoLocation.getAddress(locationCity,getApplicationContext(),new GeoHandler(),supportMapFragment);

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            String address;
            switch (msg.what){
                case 1:
                    Bundle bundle=msg.getData();
                    address=bundle.getString("address");
                    break;
                default:
                    address=null;
            }

        }
    }
}