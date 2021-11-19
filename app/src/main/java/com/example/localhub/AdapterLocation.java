package com.example.localhub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
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
        private TextView locationWeb;
        private TextView locationContact;

        public ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            locationWeb = itemView.findViewById(R.id.locationWeb);
            locationContact = itemView.findViewById(R.id.lcoationContact);

        }

        public void bind(LocationInfo locationInfo) {


            locationName.setText(locationInfo.getLocationName().get(0));

            if(locationInfo.getWeb() != null){
                locationWeb.setText(locationInfo.getWeb().get(0));
                locationWeb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationInfo.getWeb().get(0)));
                        context.startActivity(intent);
                    }
                });
            }
            if(locationInfo.getContact() != null){
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

        }
    }
}
