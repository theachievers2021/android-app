package com.example.localhub.pages;

import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.R;
import com.example.localhub.database.DatabaseHelper;
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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceListFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    private TextView location;
    private Retrofit retrofit;
    private RetrofitApiService service;
    private View view;

    private final String tag = "TEST";
    private String locationIdString = "";
    private AdapterLocation myAdapter;
    RecyclerView recyclerView;
    SearchView searchView;

    private List<LocationInfo> serviceList;
    private String locationName = "";
    private String locationDetails = "";
    private String locationContact = "";
    private String locationWeb = "";
    private String locationIdStr = "";

    public ServiceListFragment(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        location = view.findViewById(R.id.location);

        searchView = view.findViewById(R.id.searchView);
        EditText searchEditText = (EditText) view.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        ImageView searchClose = (ImageView) view.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setColorFilter(getResources().getColor(R.color.white));

        if(getArguments() != null){
            locationIdString = getArguments().getString("locationCityAndCounty");
            location.setText(locationIdString.substring(0, locationIdString.indexOf(",")));
            //Toast.makeText(getContext(),locationIdString,Toast.LENGTH_SHORT).show();


            retrofit = new Retrofit.Builder()
                    .baseUrl("https://orase.peviitor.ro")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(RetrofitApiService.class);

            //Toast.makeText(getApplicationContext(), locationStr, Toast.LENGTH_SHORT).show();
            getLocationInfo(locationIdString);
        }


    }


    private void getLocationInfo(String id) {

        Call<JsonObject> call = service.getLocationInfo("\""+id+"\"");
        Log.v(tag, "REQUEST:" +  String.valueOf(call.request()));
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject data = response.body();
                if(data.equals(null)){
                    Log.v("HUB", "SOMETHING WENT WRONG");
                }
                JsonObject responseData = (JsonObject) data.get("response");


                serviceList = new ArrayList<>();
                Gson gson = new Gson();

                JsonArray responseArray = responseData.getAsJsonArray("docs");
                if( responseArray == null || responseArray.isEmpty()){
                    Toast.makeText(getContext(), "Nu exista servicii de afisat", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
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
                    serviceList.remove(position);
                    myAdapter.notifyItemRemoved(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    if(serviceList.get(position).getLocationName() == null)
                    {
                        locationName = ".";
                    }
                    else{
                        locationName = serviceList.get(position).getLocationName().get(0);
                    }
                    if(serviceList.get(position).getContact()==null){
                        locationContact = ".";
                    }
                    else{
                        locationContact = serviceList.get(position).getContact().get(0);
                    }


                    if(serviceList.get(position).getDetails()==null){
                        locationDetails = ".";
                    }
                    else{
                        locationDetails = serviceList.get(position).getDetails().get(0);
                    }

                    if(serviceList.get(position).getWeb()==null){
                        locationWeb = ".";
                    }
                    else{
                        locationWeb += serviceList.get(position).getWeb().get(0);
                    }
                    if(serviceList.get(position).getLocationId()==null){
                        locationWeb = ".";
                    }
                    else{
                        locationWeb = serviceList.get(position).getLocationId();
                    }

                    myAdapter.notifyItemChanged(position);

                    DatabaseHelper db = new DatabaseHelper(getContext());


                    boolean b = db.addOneString(locationIdString, locationName, locationWeb, locationContact, locationDetails);

                    if(b){
                        Toast.makeText(getContext(),  locationName + "/ " + locationWeb + "/ " + locationContact + "/ " + locationDetails, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Couldn't add location", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_sweep_24)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_favorite_border_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    private void createRecyclerView(List<LocationInfo> locationInfoList){
        recyclerView = view.findViewById(R.id.recyclerViewLocation);
        myAdapter = new AdapterLocation(getContext(), locationInfoList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 1, GridLayoutManager.VERTICAL, false));
        Log.v(tag, "number services: " + locationInfoList.size());


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


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
