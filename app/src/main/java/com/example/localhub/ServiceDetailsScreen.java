package com.example.localhub;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.localhub.entities.LocationInfo;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ServiceDetailsScreen extends AppCompatActivity {


    LocationInfo service;
    LinearLayout linearLayout;
   // ServiceDatabase serviceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the requested service
        service = (LocationInfo) getIntent().getSerializableExtra("service");
      //  serviceDatabase = ServiceDatabase.getInstance(this);

        setContentView(R.layout.activity_service_details_screen);
        linearLayout = findViewById(R.id.linearLayout);

        // toolbar
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = service.getLocationName().get(0).substring(0,1).toUpperCase() + service.getLocationName().get(0).substring(1);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);

        //Floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //initialize fab
      /*  if(serviceDatabase.serviceDao().getService(service.getId()) != null){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.heart_on));
        }*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           /*    if(serviceDatabase.serviceDao().getService(service.getId()) == null){
                   fab.setImageDrawable(getResources().getDrawable(R.drawable.heart_on));
                   Toast.makeText(getApplicationContext(), "Serviciu adaugat la favorite", Toast.LENGTH_SHORT).show();
                 //  serviceDatabase.serviceDao().insertService(new ServiceEntity(service.getId()));

               }else{
                   fab.setImageDrawable(getResources().getDrawable(R.drawable.heart_off));
                   Toast.makeText(getApplicationContext(), "Serviciu scos de la favorite", Toast.LENGTH_SHORT).show();
                  // serviceDatabase.serviceDao().deleteService(service.getId());
               }*/
            }
        });

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
            }
        });



        // check what location info exists, create card with it, and insert it in the layout
        if(service.getDetails() != null){
            CardView card = createCard("Descriere", service.getDetails().get(0));
            linearLayout.addView(card);
        }

        if(service.getContact() != null) {
            CardView card = createCard("Contact", service.getContact().get(0));
            linearLayout.addView(card);

            card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + service.getContact().get(0)));
                    startActivity(intent);
                }
            });
        }

        if(service.getWeb() != null) {
            CardView card = createCard("Website", service.getWeb().get(0));
            linearLayout.addView(card);

            card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(service.getWeb().get(0)));
                    startActivity(intent);
                }
            });
        }

        if(service.getSocial() != null) {
            CardView card = createCard("Social", service.getSocial().get(0));
            linearLayout.addView(card);

            card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFacebookLink(service.getSocial().get(0));
                }
            });
        }

    }


    private void openFacebookLink(String facebookUrl){

        String pageId = facebookUrl.substring(25, facebookUrl.length() - 1);
        Toast.makeText(this, pageId, Toast.LENGTH_SHORT).show();


        String facebookId = "fb://page/153725354813480";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
        }catch (Exception e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }



    private CardView createCard(String title, String info){
        //create the card view
        CardView cardView = new CardView(this);

        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //create the linear layout
        View child = LayoutInflater.from(this).inflate(R.layout.service_details_card, null);
        TextView header = child.findViewById(R.id.header);
        TextView body = child.findViewById(R.id.body);


        //add the info to the views
        header.setText(title);
        body.setText(info);

        //add the layout to the card
        cardView.addView(child);
        return  cardView;
    }
}