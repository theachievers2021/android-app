package com.example.localhub.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localhub.domain.CurrentLocation;
import com.example.localhub.domain.LoadingDialog;
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

    private TextView insert_place;
    private Button search_place;
    private BottomNavigationView bottomNavigationView;
    private CurrentLocation currentLocation;
    private LoadingDialog loadingDialog;
    private Dialog dialog;
    private LinearLayout search_layout,second_linearLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView places_textView;
    private List<String>allIds=new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getSupportActionBar().hide();
        currentLocation=new CurrentLocation(this);
        dialog = new Dialog(HomeScreenActivity.this);


        insert_place = findViewById(R.id.insert_place);
        search_place = findViewById(R.id.search_place);
        autoCompleteTextView=findViewById(R.id.write_place);
//        InputMethodManager mgr=(InputMethodManager) getSystemService(
//                Context.INPUT_METHOD_SERVICE
//        ) ;
//        mgr.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(),0);
        //places_textView=findViewById(R.id.places_textView);




        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        loadingDialog=new LoadingDialog(this);

        search_layout=findViewById(R.id.search_layout);
        second_linearLayout=findViewById(R.id.second_linearLayout);
        second_linearLayout.setVisibility(View.INVISIBLE);

        //perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        return false;
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(),FavoritesActivity.class));
                        //overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.current_place:
                        loadingDialog.startLoadingDialog();
                        currentLocation.current_location();
                        if(currentLocation.isLocationFound()) {
                            loadingDialog.dismissDialog();
                            //finish();
                            return true;
                        }
                        else{
                            loadingDialog.dismissDialog();
                            return false;
                        }
                    case R.id.add:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://theachievers2021.github.io/localhub/"));
                        startActivity(intent);
                        return false;

                }
                return false;

            }
        });




        configure_inputText();
        introduce_place();
        search_place();



    }


    private void introduce_place(){



        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://zimbor.go.ro/solr/romania/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LocationRepository locationApi=retrofit.create(LocationRepository.class);

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String text=autoCompleteTextView.getText().toString();
                Call<JsonObject> call=locationApi.getLocation('\"'+text+'\"',true,"OR","*:*");
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
                                    //places_textView.append("\n"+ids[1]);

                                }
                                System.out.println("id: "+ids[1]);


                            }

                            System.out.println(allIds);
                            ArrayAdapter<String> placesAdapter=new ArrayAdapter<>(HomeScreenActivity.this, R.layout.layout_item_autocomplete,R.id.td_Custom,allIds);
                            autoCompleteTextView.setAdapter(placesAdapter);
                            //places_textView.setText(allIds.get(position));
                            System.out.println("places:");
                            //System.out.println(placesAdapter.getItem(position));
                            //allIds.forEach(x->places_textView.append(x));


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

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View v = getCurrentFocus();
                if(v!=null){
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
                String text=autoCompleteTextView.getText().toString();
                search_layout.setVisibility(View.VISIBLE);
                insert_place.setText(text);
                second_linearLayout.setVisibility(View.INVISIBLE);


            }
        });
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
//        List<String>allIds=new ArrayList<>();
//
//
//        Retrofit retrofit=new Retrofit.Builder()
//                .baseUrl("http://zimbor.go.ro/solr/romania/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        LocationRepository locationApi=retrofit.create(LocationRepository.class);



        insert_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_layout.setVisibility(View.INVISIBLE);
                second_linearLayout.setVisibility(View.VISIBLE);

//                Window window = dialog.getWindow();
//                WindowManager.LayoutParams wlp = window.getAttributes();
//
//                wlp.gravity = Gravity.TOP;
//                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//                window.setAttributes(wlp);//set custom dialog
//                dialog.setContentView(R.layout.dialog_searchable_spinner);
//                //dialog.getWindow().setLayout(900,1000);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();


                //allIds.clear();
//                Call<JsonObject> call=locationApi.getLocation('\"'+textView.getText().toString()+'\"',true,"OR","*:*");
//                call.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        if (!response.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
//                            System.out.println("Eroare");
//                            return;
//                        } else {
//                            JsonObject data = response.body();
//
//                            JsonObject responseData = (JsonObject) data.get("response");
//                            int numLocations = responseData.getAsJsonArray("docs").size();
//                            for(int i=0;i<numLocations;i++){
//                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
//                                String[] ids=String.valueOf(locationData.get("id")).split("\"");
//                                if(!allIds.contains(ids[1])) {
//                                    allIds.add(ids[1]);
//                                }
//                                System.out.println("id: "+ids[1]);
//
//                            }
//
//                            System.out.println(allIds);
//                            ArrayAdapter<String> placesAdapter=new ArrayAdapter<>(HomeScreenActivity.this, android.R.layout.simple_list_item_1,allIds);
//                            insert_place.setAdapter(placesAdapter);
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
//                        System.out.println("Something went wrong...");
//                    }
//                });

            }
        });
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
//        String text = autoCompleteTextView.getText().toString();
//        this.search_layout.setVisibility(View.VISIBLE);
//        this.insert_place.setText(text);
//        this.second_linearLayout.setVisibility(View.INVISIBLE);
    }

//    public void onConfigurationChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        String text = autoCompleteTextView.getText().toString();
//        this.search_layout.setVisibility(View.VISIBLE);
//        this.insert_place.setText(text);
//        this.second_linearLayout.setVisibility(View.INVISIBLE);
//

//    }
}