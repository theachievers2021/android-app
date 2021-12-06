package com.example.localhub;

import android.content.Intent;
import android.os.Bundle;

import com.example.localhub.database.DatabaseHelper;
import com.example.localhub.entities.LocationInfo;
import com.example.localhub.repos.RetrofitApiService;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import retrofit2.Retrofit;

public class FavoriteActivity extends AppCompatActivity {

    private AdapterLocation myAdapter;
    RecyclerView recyclerView;
    List<LocationInfo> locationInfoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewLocation);
        myAdapter = new AdapterLocation(this, locationInfoList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getApplicationContext(), 1, GridLayoutManager.VERTICAL, false));

    }


}