package com.example.localhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FavoritesScreen extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CurrentLocation currentLocation;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        loadingDialog=new LoadingDialog(this);
//        getSupportActionBar().hide();
      //  currentLocation = HomeScreen.getHomeScreenCurrentLocation();

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //set favorites selected
        bottomNavigationView.setSelectedItemId(R.id.favorites);

        //perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                      /*  startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        overridePendingTransition(0,0);*/
                        finish();
                        return true;
                    case R.id.favorites:
                        return false;
                    case R.id.current_place:
                        loadingDialog.startLoadingDialog();
                        currentLocation.current_location();
                        if(currentLocation.isLocationFound()) {
                            finish();
                            loadingDialog.dismissDialog();
                            return true;
                        }
                        else
                            return false;
                    case R.id.add:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_localhub)));
                        startActivity(intent);
                        return false;

                }
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
    }

}