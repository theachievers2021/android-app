package com.example.localhub.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.localhub.entities.CurrentLocation;
import com.example.localhub.entities.LoadingDialog;
import com.example.localhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment;
    HomeFragment homeFragment;
    ServiceListFragment servicesFragmnetSearch, servicesFragmnetLocation;
    FavoritesFragment favoritesFragment;
    CurrentLocation currentLocation;
    FragmentManager fragmentManager;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
        loadingDialog = new LoadingDialog(this);

        servicesFragmnetSearch = new ServiceListFragment(bottomNavigationView, loadingDialog);
        servicesFragmnetLocation = new ServiceListFragment(bottomNavigationView, loadingDialog);
        currentLocation = new CurrentLocation(this, servicesFragmnetLocation, fragmentManager);
        homeFragment = new HomeFragment(servicesFragmnetSearch, fragmentManager);
        favoritesFragment = new FavoritesFragment(bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        selectedFragment = homeFragment;

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home: selectedFragment = homeFragment;
                                    break;
                    case R.id.current_place: selectedFragment = servicesFragmnetLocation;
                                    currentLocation.current_location();
                                    if(!currentLocation.isLocationFound()){
                                        loadingDialog.startLoadingDialog();
                                        return false;
                                    }
                                   break;
                    case R.id.favorites: selectedFragment = favoritesFragment;
                                    break;
                    case R.id.add: Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_localhub)));
                                    startActivity(intent);
                                    return false;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }

}