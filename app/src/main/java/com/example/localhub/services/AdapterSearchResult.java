/*
package com.example.localhub.services;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.R;

import java.util.List;

public class AdapterSearchResult extends RecyclerView.Adapter<AdapterSearchResult.ViewHolder> {

    String tag = "ADAPTER";
    private Context context;
    private List<String> idsList;

    public AdapterSearchResult(Context context, List<String> idsList) {
        this.context = context;
        this.idsList = idsList;

    }

    @NonNull
    @Override
    public AdapterSearchResult.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_location_item, parent, false);
        return new AdapterSearchResult.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterSearchResult.ViewHolder holder, int position) {
        String locationInfo = idsList.get(position);
        holder.bind(locationInfo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open second activity
                Intent i = new Intent(context, ListServicesScreen.class);
                i.putExtra("locationId", locationInfo);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return idsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView location;

        public ViewHolder(View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.locationString);
        }

        public void bind(String locationInfo) {

            if(locationInfo != null){
                location.setText(locationInfo);
            }
        }
    }
}
*/
