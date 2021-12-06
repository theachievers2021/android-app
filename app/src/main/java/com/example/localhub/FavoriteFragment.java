package com.example.localhub;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.localhub.database.DatabaseHelper;
import com.example.localhub.entities.LocationInfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment{

    public FavoriteFragment() {
    }

    private AdapterLocation myAdapter;
    RecyclerView recyclerView;
    List<LocationInfo> locationInfoList;

    List<String> locationId;

    List<String> locationName;

    List<String> web;

    List<String> contact;

    List<String> details;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        DatabaseHelper db = new DatabaseHelper(getActivity());
        locationId = db.getLocationId();
        locationName = db.getLocationName();
        web = db.getLocationWeb();
        contact = db.getLocationContact();
        details = db.getLocationDetails();

        List<String> locationNameList = new ArrayList<>();

        List<String> webList = new ArrayList<>();

        List<String> contactList = new ArrayList<>();

        List<String> detailsList = new ArrayList<>();
        for(int i = 0; i< locationName.size(); i++){
            locationNameList.add(locationName.get(i));
            webList.add(web.get(i));
            contactList.add(contact.get(i));
            detailsList.add(details.get(i));
            LocationInfo locInfo = new LocationInfo(locationId.get(i), locationNameList, webList, contactList, detailsList );
            locationInfoList.add(locInfo);
        }


        TextView txtView = view.findViewById(R.id.favTxt);
        txtView.setText(locationName.toString());
        return view;
    }
}