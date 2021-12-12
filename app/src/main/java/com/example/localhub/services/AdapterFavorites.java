package com.example.localhub.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.R;

import java.util.List;

public class AdapterFavorites extends RecyclerView.Adapter<AdapterFavorites.FavViewHolder> {

    String tag = "ADAPTER";
    private Context context;
    private List<String> locationNames;
    private List<String> locationDetails;
    private List<String> locationContact;
    private List<String> locationWeb;
    private List<String> locationIdStr;
    private List<String> locationSocials;
    private List<String> locationGps;


    public AdapterFavorites(Context context, List<String> locationNames, List<String> locationDetails, List<String> locationContact, List<String> locationWeb, List<String> locationIdStr,List<String> locationSocials, List<String>locationGps) {
        this.locationNames = locationNames;
        this.locationDetails = locationDetails;
        this.locationContact = locationContact;
        this.locationWeb = locationWeb;
        this.locationIdStr = locationIdStr;
        this.locationSocials = locationSocials;
        this.locationGps = locationGps;
        this.context = context;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
        return new AdapterFavorites.FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        String currentLocationName = locationNames.get(position);
        String currentLocationDetails = locationDetails.get(position);
        String currentLocationContact = locationContact.get(position);
        String currentLocationWeb = locationWeb.get(position);
        String currentIdStr = locationIdStr.get(position);
        String currentSocial = locationSocials.get(position);
        String currentGps = locationGps.get(position);
        holder.bind(currentLocationName, currentLocationWeb, currentLocationDetails, currentIdStr, currentLocationContact, currentSocial, currentGps);
    }

    @Override
    public int getItemCount() {
        return locationNames.size();
    }

    class FavViewHolder extends RecyclerView.ViewHolder{

        private TextView locationName;
        private View currentView;
        private ImageView imageView;
        LinearLayout linearLayout;
        private boolean showDetails;
        private boolean existDetails;
        private String messageString = "Nu exista detalii de afisat";
        String[] coordinates;

        public FavViewHolder(@NonNull View itemView) {

            super(itemView);

            locationName = itemView.findViewById(R.id.locationName);
            currentView = itemView;
            linearLayout = itemView.findViewById(R.id.serviceDetails);
            showDetails = false;
            existDetails = false;
        }

        public void bind(String locationNameStr, String locationWebStr, String locationDetailsStr, String locationIdStr, String locationContactStr, String locationSocialStr, String locationGpsStr) {

            if (locationNameStr != null) {
                String nume = locationNameStr;
                locationName.setText(nume);
            }

            if (locationIdStr != null) {
                CardView card = createCard("Oras", locationIdStr);
                linearLayout.addView(card);
                existDetails = true;
            }

            if (locationDetailsStr != null) {
                CardView card = createCard("Descriere", locationDetailsStr);
                linearLayout.addView(card);
                existDetails = true;
            }

            if (locationContactStr != null) {
                CardView card = createCard("Contact", locationContactStr);
                linearLayout.addView(card);
                existDetails = true;


                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + locationContactStr));
                        context.startActivity(intent);
                    }
                });
            }

            if (locationWebStr != null) {
                CardView card = createCard("Website", locationWebStr);
                linearLayout.addView(card);
                existDetails = true;

                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationWebStr));
                        context.startActivity(intent);
                    }
                });
            }

            if (locationSocialStr != null) {
                CardView card = createCard("Social", locationSocialStr);
                linearLayout.addView(card);
                existDetails = true;


                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFacebookLink(locationSocialStr);
                    }
                });
            }

            if (locationGpsStr != null) {
                CardView card = createCard("Locatie", "Vezi pe harta");
                linearLayout.addView(card);
                existDetails = true;
                coordinates = locationGpsStr.split(",");

                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri.Builder directionsBuilder = new Uri.Builder()
                                .scheme("https")
                                .authority("www.google.com")
                                .appendPath("maps")
                                .appendPath("dir")
                                .appendPath("")
                                .appendQueryParameter("api", "1")
                                .appendQueryParameter("destination", coordinates[0] + "," + coordinates[1]);

                        Log.v(tag, directionsBuilder.toString());
                        context.startActivity(new Intent(Intent.ACTION_VIEW, directionsBuilder.build()));
                    }
                });
            }


            if (!existDetails) {
                TextView message = createTextView(messageString);
                linearLayout.addView(message);
            }


            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetails = !showDetails;

                    if (showDetails) {
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        linearLayout.setVisibility(View.GONE);
                    }

                }
            });
        }

        private void openFacebookLink(String facebookUrl){

            String pageId = facebookUrl.substring(25, facebookUrl.length() - 1);
            Toast.makeText(context, pageId, Toast.LENGTH_SHORT).show();

            String facebookId = "fb://page/153725354813480";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
            }catch (Exception e){
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }
        }


        private CardView createCard(String title, String info){
            CardView cardView = new CardView(context);

            cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            View child = LayoutInflater.from(context).inflate(R.layout.service_details_card, null);
            TextView header = child.findViewById(R.id.header);
            TextView body = child.findViewById(R.id.body);

            header.setText(title);
            body.setText(info);

            cardView.addView(child);
            return  cardView;
        }

        private TextView createTextView(String message){

            TextView textView = new TextView(context);

            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(message);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

            return textView;
        }
    }
}

