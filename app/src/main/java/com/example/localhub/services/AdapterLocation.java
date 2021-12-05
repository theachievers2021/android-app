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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.R;
import com.example.localhub.entities.LocationInfo;

import java.util.List;

public class AdapterLocation extends RecyclerView.Adapter<AdapterLocation.ViewHolder> {

    String tag = "ADAPTER";
    private Context context;
    private List<LocationInfo> locationInfoList;

    public AdapterLocation(Context context, List<LocationInfo> locationInfoList) {
        this.context = context;
        this.locationInfoList = locationInfoList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
        return new AdapterLocation.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterLocation.ViewHolder holder, int position) {
        LocationInfo locationInfo = locationInfoList.get(position);
        holder.bind(locationInfo);
    }

    @Override
    public int getItemCount() {
        return locationInfoList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private View currentView;
        private ImageView imageView;
        LinearLayout linearLayout;
        private boolean showDetails;
        private boolean existDetails;
        private String messageString = "Nu exista detalii de afisat";
        String[] coordinates;

        public ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            imageView = itemView.findViewById(R.id.locationImage);
            currentView = itemView;
            linearLayout = itemView.findViewById(R.id.serviceDetails);
            showDetails = false;
            existDetails = false;

        }

        public void bind(LocationInfo locationInfo) {

            String nume = locationInfo.getLocationName().get(0).substring(0,1).toUpperCase() + locationInfo.getLocationName().get(0).substring(1);
            locationName.setText(nume);


            // check what location info exists, create card with it, and insert it in the layout
            if(locationInfo.getDetails() != null){
                CardView card = createCard("Descriere", locationInfo.getDetails().get(0));
                linearLayout.addView(card);
                existDetails = true;
            }

            if(locationInfo.getContact() != null) {
                CardView card = createCard("Contact", locationInfo.getContact().get(0));
                linearLayout.addView(card);
                existDetails = true;


                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + locationInfo.getContact().get(0)));
                        context.startActivity(intent);
                    }
                });
            }

            if(locationInfo.getWeb() != null) {
                CardView card = createCard("Website", locationInfo.getWeb().get(0));
                linearLayout.addView(card);
                existDetails = true;

                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationInfo.getWeb().get(0)));
                        context.startActivity(intent);
                    }
                });
            }

            if(locationInfo.getSocial() != null) {
                CardView card = createCard("Social", locationInfo.getSocial().get(0));
                linearLayout.addView(card);
                existDetails = true;


                card.getChildAt(0).findViewById(R.id.body).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFacebookLink(locationInfo.getSocial().get(0));
                    }
                });
            }

            if(locationInfo.getGpsCoordinates() != null){
                CardView card = createCard("Locatie", "Vezi pe harta");
                linearLayout.addView(card);
                existDetails = true;
                coordinates = locationInfo.getGpsCoordinates().get(0).split(",");

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

            if(!existDetails){
                TextView message = createTextView(messageString);
                linearLayout.addView(message);
            }
            ///////////////////////////////////////////////




            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(context, ServiceDetailsScreen.class);
                    intent.putExtra("service",locationInfo);
                    context.startActivity(intent);*/

                    showDetails = !showDetails;

                    if(showDetails){
                        linearLayout.setVisibility(View.VISIBLE);
                    }else{
                        linearLayout.setVisibility(View.GONE);
                    }

                }
            });

          /*  if(locationInfo.getWeb() != null){
                locationWeb.setText(locationInfo.getWeb().get(0));
                locationWeb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationInfo.getWeb().get(0)));
                        context.startActivity(intent);
                    }
                });
            }

            StringBuilder sb;
             if(locationInfo.getContact() != null) {
                 locationContact.setText(locationInfo.getContact().get(0));
                   locationContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+locationInfo.getContact().get(0)));
                        context.startActivity(intent);
                    }
                });
            }


            sb = new StringBuilder();
            if(locationInfo.getDetails() != null){
                for (String detail: locationInfo.getDetails()
                     ) {
                    sb.append(detail + "\n");
                }
                sb.deleteCharAt(sb.length() - 1);
                locationDetails.setText(sb.toString());

            }

            if(locationInfo.getSocial() != null) {
                locationSocial.setText(locationInfo.getSocial().get(0));
                locationSocial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFacebookLink(locationInfo.getSocial().get(0));
                    }
                });
            }*/

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
            //create the card view
            CardView cardView = new CardView(context);

            cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //create the linear layout
            View child = LayoutInflater.from(context).inflate(R.layout.service_details_card, null);
            TextView header = child.findViewById(R.id.header);
            TextView body = child.findViewById(R.id.body);


            //add the info to the views
            header.setText(title);
            body.setText(info);

            //add the layout to the card
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
