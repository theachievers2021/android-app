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
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localhub.domain.CurrentLocation;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.example.localhub.R;
import com.example.localhub.repository.LocationRepository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenActivity extends AppCompatActivity{

    private AutoCompleteTextView insert_place;
    private Button search_place;
    private BottomNavigationView bottomNavigationView;
    private CurrentLocation currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getSupportActionBar().hide();
        currentLocation=new CurrentLocation(this);


        insert_place = findViewById(R.id.insert_place);
        search_place = findViewById(R.id.search_place);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        return false;
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(),FavoritesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.current_place:
                        currentLocation.current_location();
                        if(currentLocation.isLocationFound()) {
                            finish();
                            return true;
                        }
                        else
                            return false;
                    case R.id.add:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://theachievers2021.github.io/localhub/"));
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

}