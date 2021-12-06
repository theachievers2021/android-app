package com.example.localhub;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.database.DatabaseHelper;
import com.example.localhub.entities.LocationId;
import com.example.localhub.entities.LocationInfo;
import com.example.localhub.entities.LocationResult;
import com.example.localhub.repos.RetrofitApiService;

import java.util.List;


import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondScreen extends AppCompatActivity {

    private final String tag = "TEST";
    private Intent intent;
    private Retrofit retrofit;
    private RetrofitApiService service;
    private String locationIdString = null;
    private AdapterLocation myAdapter;
    private List<LocationInfo> locationInfoList;

    private String locationName = "";
    private String locationDetails = "";
    private String locationContact = "";
    private String locationWeb = "";
    private String locationIdStr = "";
    Button testBtn;

    RecyclerView recyclerView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);


        testBtn = findViewById(R.id.btnTest);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment favFragment = new FavoriteFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, favFragment).commit();
            }
        });

        //retrofit and service initialization
        retrofit = new Retrofit.Builder()
                .baseUrl("https://orase.peviitor.ro")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitApiService.class);


        //Get the location from the previous activity
        intent = getIntent();
        @SuppressWarnings("unchecked")
        List<String> location = (List<String>) intent.getSerializableExtra("locationCityAndCounty");
        String idSearchResult = intent.getStringExtra("locationId");



        if(idSearchResult != null){
            Log.v(tag, idSearchResult);
            idSearchResult = idSearchResult.replace("\"","");
           getLocationInfo(idSearchResult);
           this.setTitle(idSearchResult);

        }else{
            String county = location.get(1);
            location.set(1,county.substring(0, county.indexOf(' ')));
            getLocationId(location.get(0), location.get(1));
            this.setTitle(location.get(0)+", judet " + location.get(0));
        }





    }

    private void getLocationId(String city, String county){
        //get the location id
        Call<LocationId> call = service.getLocationId(city, county);
        // Call<LocationId> call = service.getLocationId("Zimbor", "Salaj");
        call.enqueue(new Callback<LocationId>() {
            @Override
            public void onResponse(Call<LocationId> call, Response<LocationId> response) {
                LocationId locationId = response.body();
                locationIdString =  locationId.getId();
                getLocationInfo(locationIdString);
            }

            @Override
            public void onFailure(Call<LocationId> call, Throwable t) {
                locationIdString =  "ERROR";
                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocationInfo(String id) {

        //encode the location string
        id = id.replace(" ","%20");
        String locationid = "%22" + id + "%22";

        Call<LocationResult> call = service.getLocationInfo(locationid);
        Log.v(tag, String.valueOf(call.request()));
        call.enqueue(new Callback<LocationResult>() {
            @Override
            public void onResponse(Call<LocationResult> call, Response<LocationResult> response) {
                LocationResult locationResult = response.body();
                if(locationResult.equals(null)){
                    Log.v(tag, "SOMETHING WENT WRONG");
                }
                locationInfoList = locationResult.getResponse().getLocationInfoList();
                createRecyclerView(locationInfoList);

            }

            @Override
            public void onFailure(Call<LocationResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
            }
        });
    }



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    locationInfoList.remove(position);
                    myAdapter.notifyItemRemoved(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    if(locationInfoList.get(position).getLocationName() == null)
                    {
                        locationName = ".";
                    }
                    else{
                        locationName = locationInfoList.get(position).getLocationName().get(0);
                    }
                    if(locationInfoList.get(position).getContact()==null){
                        locationContact = ".";
                    }
                    else{
                        locationContact = locationInfoList.get(position).getContact().get(0);
                    }


                    if(locationInfoList.get(position).getDetails()==null){
                        locationDetails = ".";
                    }
                    else{
                        locationDetails = locationInfoList.get(position).getDetails().get(0);
                    }

                    if(locationInfoList.get(position).getWeb()==null){
                        locationWeb = ".";
                    }
                    else{
                        locationWeb += locationInfoList.get(position).getWeb().get(0);
                    }
                    if(locationInfoList.get(position).getLocationId()==null){
                        locationWeb = ".";
                    }
                    else{
                        locationWeb = locationInfoList.get(position).getLocationId();
                    }

                    myAdapter.notifyItemChanged(position);

                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());


                    boolean b = db.addOneString(locationIdStr, locationName, locationWeb, locationContact, locationDetails);

                    if(b){
                        Toast.makeText(getApplicationContext(), locationIdStr + "/ " + locationName + "/ " + locationWeb + "/ " + locationContact + "/ " + locationDetails, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Couldn't add location", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_sweep_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(SecondScreen.this, R.color.teal_200))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_favorite_border_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(SecondScreen.this, R.color.purple_200))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    private void createRecyclerView(List<LocationInfo> locationInfoList){
        recyclerView = findViewById(R.id.recyclerViewLocation);
        myAdapter = new AdapterLocation(SecondScreen.this, locationInfoList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 1, GridLayoutManager.VERTICAL, false));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        searchView = findViewById(R.id.searchViewSecondScreen);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                myAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
}