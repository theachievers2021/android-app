package com.example.localhub;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.entities.LocationInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterLocation extends RecyclerView.Adapter<AdapterLocation.ViewHolder> implements Filterable {

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

    @Override
    public Filter getFilter() {
        return locationFilter;
    }

    private Filter locationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LocationInfo> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 ){
                filteredList.addAll(locationInfoList);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(LocationInfo l: locationInfoList)
                        if(l.getLocationName().get(0).toLowerCase().contains(filterPattern)){
                            filteredList.add(l);
                        }
                    }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            locationInfoList.clear();
            locationInfoList.addAll((List) results.values);
            notifyDataSetChanged();

        }

    };


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView locationName;
        private TextView locationWeb;
        private TextView locationContact;
        private TextView locationDetails;
        private TextView locationSocial;

        public ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            locationWeb = itemView.findViewById(R.id.locationWeb);
            locationContact = itemView.findViewById(R.id.lcoationContact);
            locationDetails = itemView.findViewById(R.id.locationDetails);
            locationSocial = itemView.findViewById(R.id.locationSocial);

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
            }

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

           /* try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);

                if (applicationInfo.enabled) {
                    int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    String url;

                    if (versionCode >= 3002850) {
                        url = "fb://facewebmodal/f?href=" + facebookUrl;
                    } else {
                        url = "fb://page/" + pageId;
                    }

                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else {
                    throw new Exception("Facebook is disabled");
                }
            } catch (Exception e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }*/
        }

    }
}
