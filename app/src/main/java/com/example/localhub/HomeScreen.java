//package com.example.localhub;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localhub.repos.LocationRepository;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
/*

public class HomeScreen extends AppCompatActivity{

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
          */
/*  while(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //requestLocation();
            }*//*


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

                    Intent intent = new Intent(getApplicationContext(), ListServicesScreen.class);
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

                            AdapterSearchResult adapterSearchResult = new AdapterSearchResult(HomeScreen.this, allIds);
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

}*/




//////////////////////// ANA

/*public class HomeScreen extends AppCompatActivity{

    private AutoCompleteTextView insert_place;
    private Button search_place;
    private BottomNavigationView bottomNavigationView;
    private static CurrentLocation currentLocation;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        loadingDialog = new LoadingDialog(this);

       // getSupportActionBar().hide();
        currentLocation=new CurrentLocation(this);


        insert_place = findViewById(R.id.insert_place);
        search_place = findViewById(R.id.search_place);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //perform ItemSelectedListener
        Log.v("PLANG", "CREATE");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        return false;
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(),FavoritesScreen.class));
                        overridePendingTransition(0,0);
                        //finish();
                        return true;
                    case R.id.current_place:
                        loadingDialog.startLoadingDialog();
                        currentLocation.current_location();
                        if(currentLocation.isLocationFound()) {
                          //  finish();
                            loadingDialog.dismissDialog();
                            return true;
                        }else{
                            return false;
                        }
                    case R.id.add:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_localhub)));
                        startActivity(intent);
                        return false;

                }
                return false;

            }
        });


        configure_inputText();
        search_place();


    }





    private void search_place() {
        search_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeScreenActivity.this,"ok.",Toast.LENGTH_SHORT).show();
                System.out.println("Buton apasat");
                String location = insert_place.getText().toString();
                if(location.equals("")){
                    Toast.makeText(HomeScreen.this,"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(HomeScreen.this,"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

                                }else {
                                    //Toast.makeText(HomeScreenActivity.this, location, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ListServicesScreen.class);
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
                            ArrayAdapter<String> placesAdapter=new ArrayAdapter<>(HomeScreen.this, android.R.layout.simple_list_item_1,allIds);
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
    protected void onResume() {
        super.onResume();
        if(loadingDialog != null){
            if(loadingDialog.isShowing()) loadingDialog.dismissDialog();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    public static CurrentLocation getHomeScreenCurrentLocation(){
        return currentLocation;
    }
}*/

