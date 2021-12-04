package com.example.localhub.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.localhub.R;
import com.example.localhub.domain.CurrentLocation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FavoritesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CurrentLocation currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().hide();
        currentLocation=new CurrentLocation(this);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //set favorites selected
        bottomNavigationView.setSelectedItemId(R.id.favorites);

        //perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeScreenActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.favorites:
                        return false;
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
    }


}