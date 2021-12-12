package com.example.localhub.pages;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.localhub.R;
import com.example.localhub.repos.LocationRepository;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private TextView insert_place;
    private Button search_place;
    ServiceListFragment serviceListFragment;
    private FragmentManager fragmentManager;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout search_layout,second_linearLayout;
    private List<String>allIds = new ArrayList<>();
    Retrofit retrofit;

    public HomeFragment(ServiceListFragment servicesFragmnet, FragmentManager fragmentManager) {
        this.serviceListFragment = servicesFragmnet;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        insert_place = view.findViewById(R.id.insert_place);
        search_place = view.findViewById(R.id.search_place);
        autoCompleteTextView = view.findViewById(R.id.write_place);
        search_layout = view.findViewById(R.id.search_layout);
        second_linearLayout = view.findViewById(R.id.second_linearLayout);

         retrofit = new Retrofit.Builder()
                .baseUrl("https://orase.peviitor.ro/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        configure_inputText();
        introduce_place();
        search_place();
    }

    private void introduce_place(){
        LocationRepository locationApi = retrofit.create(LocationRepository.class);

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String text = autoCompleteTextView.getText().toString();
                Call<JsonObject> call = locationApi.getLocation(text);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            System.out.println("Eroare");
                            return;
                        } else {
                            JsonObject data = response.body();

                            JsonObject responseData = (JsonObject) data.get("response");
                            int numLocations = responseData.getAsJsonArray("docs").size();
                            for (int i = 0; i < numLocations; i++) {
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String[] ids = String.valueOf(locationData.get("id")).split("\"");
                                if (!allIds.contains(ids[1])) {
                                    allIds.add(ids[1]);
                                }
                            }

                            System.out.println(allIds);
                            ArrayAdapter<String> placesAdapter = new ArrayAdapter<>(getContext(), R.layout.layout_item_autocomplete, R.id.td_Custom, allIds);
                            autoCompleteTextView.setAdapter(placesAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        System.out.println("Something went wrong...");
                    }
                });
                return false;
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String text = autoCompleteTextView.getText().toString();
                search_layout.setVisibility(View.VISIBLE);
                insert_place.setText(text);
                second_linearLayout.setVisibility(View.INVISIBLE);
                search_place.setVisibility(View.VISIBLE);
                hideKeyboard(autoCompleteTextView);

            }
        });
    }

    private void search_place() {
        search_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Buton apasat");
                String location = insert_place.getText().toString();
                if(location.equals("")){
                    Toast.makeText(getContext(),"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();
                }
                else{
                    LocationRepository locationApi=retrofit.create(LocationRepository.class);
                    Call<JsonObject> call=locationApi.getLocation(location);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                System.out.println("Eroare");
                                return;
                            } else {
                                JsonObject data = response.body();

                                JsonObject responseData = (JsonObject) data.get("response");
                                int numLocations = responseData.getAsJsonArray("docs").size();
                                if(numLocations==0){
                                    Toast.makeText(getContext(),"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

                                }else {
                                    Bundle args = new Bundle();
                                    args.putString("locationCityAndCounty", location);
                                    serviceListFragment.setArguments(args);
                                    FragmentTransaction transaction =  fragmentManager.beginTransaction();
                                    transaction.replace(R.id.fragment_container, serviceListFragment);
                                    transaction.disallowAddToBackStack();
                                    transaction.commit();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            System.out.println("Something went wrong...");
                        }
                    });
                }
            }
        });
    }


    private void configure_inputText(){
        second_linearLayout.setVisibility(View.INVISIBLE);
        search_place.setVisibility(View.INVISIBLE);

        insert_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_layout.setVisibility(View.INVISIBLE);
                second_linearLayout.setVisibility(View.VISIBLE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                showKeyboard(autoCompleteTextView);
            }
        });
    }

    private void hideKeyboard(AutoCompleteTextView autoCompleteTextView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getApplicationWindowToken(), 0);
    }

    private void showKeyboard(AutoCompleteTextView autoCompleteTextView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(autoCompleteTextView.getRootView(), InputMethodManager.SHOW_IMPLICIT);
        autoCompleteTextView.requestFocus();
    }

}
