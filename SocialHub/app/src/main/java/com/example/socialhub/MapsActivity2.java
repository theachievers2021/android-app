package com.example.socialhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.socialhub.databinding.ActivityMaps2Binding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private SearchView searchView;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private String locationCity="";
    private LatLng latLngCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchView=findViewById(R.id.sv_location);
        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        grantPermission();
        checkLocationIsEnabled();
        getLocation();
        Log.d("HUB","Aici: "+latLngCity);
        System.out.println("Ok");


//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location=searchView.getQuery().toString();
//
//                List<Address> addressList=null;
//                if(location!=null && !location.equals("")){
////                    GeoLocation geoLocation=new GeoLocation();
//                    Geocoder geocoder=new Geocoder(MapsActivity2.this);
//                    try{
//                        addressList=geocoder.getFromLocationName(location,1);
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }
//                    Address address=addressList.get(0);
//                    LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
////                    geoLocation.getAddress(location,getApplicationContext(),new GeoHandler(),mapFragment);
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        Log.d("HUB",locationCity);
//        mMap.addMarker(new MarkerOptions().position(latLngCity).title("My location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCity));
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
            new AlertDialog.Builder(MapsActivity2.this)
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
                    Toast.makeText(MapsActivity2.this,"Unable to find location. Search by city.", Toast.LENGTH_SHORT).show();
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
            latLngCity=new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("HUB",locationCity);
            Log.d("HUB","Aixi: "+latLngCity);
            GeoLocation geoLocation=new GeoLocation();
            geoLocation.getAddress(locationCity,getApplicationContext(),new GeoHandler(),mapFragment);
            double bottomBoundary=location.getLatitude()-0.3;
            double leftBoudary=location.getLongitude()-0.3;
            double topBoudary=location.getLatitude()+0.3;
            double rightBoundary=location.getLongitude()+0.3;
            LatLngBounds bounds=new LatLngBounds(new LatLng(bottomBoundary,leftBoudary),new LatLng(topBoudary,rightBoundary));
            mMap.addMarker(new MarkerOptions().position(latLngCity).title(locationCity));
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,400,400,1));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCity,10));


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

