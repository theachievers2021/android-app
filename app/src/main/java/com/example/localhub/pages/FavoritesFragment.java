package com.example.localhub.pages;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localhub.R;
import com.example.localhub.database.DatabaseHelper;
import com.example.localhub.services.AdapterFavorites;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FavoritesFragment extends Fragment {
    private final String tag = "TEST";
    private AdapterFavorites myAdapter;
    private List<String> locationNames;
    private List<String> locationDetails;
    private List<String> locationContact;
    private List<String> locationWeb;
    private List<String> locationIdStr;
    private List<String> locationSocials;
    private List<String> locationGps;
    private List<Integer> ids;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    View view;

    public FavoritesFragment(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        DatabaseHelper db = new DatabaseHelper(getContext());

        locationNames = db.getLocationName();
        locationDetails = db.getLocationDetails();
        locationContact = db.getLocationContact();
        locationWeb = db.getLocationWeb();
        locationIdStr = db.getLocationId();
        locationSocials = db.getLocationSocials();
        locationGps = db.getLocationGps();

        ids = db.getId();

        recyclerView = view.findViewById(R.id.recyclerViewFav);
        myAdapter = new AdapterFavorites(getContext(), locationNames, locationDetails, locationContact, locationWeb, locationIdStr, locationSocials, locationGps);

        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 1, GridLayoutManager.VERTICAL, false));
        Log.v(tag, "number services: " + locationNames.size());

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        locationIdStr.remove(position);
                        locationGps.remove(position);
                        locationDetails.remove(position);
                        locationWeb.remove(position);
                        locationContact.remove(position);
                        locationNames.remove(position);
                        locationSocials.remove(position);

                        myAdapter.notifyItemRemoved(position);
                        DatabaseHelper dbd = new DatabaseHelper(getContext());
                        boolean b = dbd.deleteOne(ids.get(position));

                        if(!b){
                            Toast.makeText(getContext(),  "Serviciu sters de la Favorite", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Serviciul nu a putut fi sters", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_sweep_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
