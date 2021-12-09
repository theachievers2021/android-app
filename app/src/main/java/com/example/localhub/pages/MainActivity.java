package com.example.localhub.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.localhub.CurrentLocation;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav_with_fragments);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        //init
        servicesFragmnetSearch = new ServiceListFragment(bottomNavigationView);
        servicesFragmnetLocation = new ServiceListFragment(bottomNavigationView);
        currentLocation = new CurrentLocation(this, servicesFragmnetLocation, fragmentManager);
        homeFragment = new HomeFragment(servicesFragmnetSearch, fragmentManager);
        favoritesFragment = new FavoritesFragment();


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        selectedFragment = homeFragment;
        //bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home: selectedFragment = homeFragment;
                                    break;
                    case R.id.current_place: selectedFragment = servicesFragmnetLocation;
                                    currentLocation.current_location();
                                    if(!currentLocation.isLocationFound()){
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