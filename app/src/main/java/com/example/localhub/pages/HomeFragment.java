package com.example.localhub.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    private AutoCompleteTextView insert_place;
    private Button search_place;
    ServiceListFragment serviceListFragment;
    private FragmentManager fragmentManager;

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

        configure_inputText();
        search_place();
    }



    private void search_place() {
        search_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeScreenActivity.this,"Buton apasat",Toast.LENGTH_SHORT).show();
                String location = insert_place.getText().toString();
                if(location.equals("")){
                    Toast.makeText(getContext(),"Locatia nu poate fi gasita.",Toast.LENGTH_SHORT).show();

                }
                else{
                    List<String> allIds = new ArrayList<>();

                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl("http://zimbor.go.ro/solr/romania/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    LocationRepository locationApi=retrofit.create(LocationRepository.class);
                    Call<JsonObject> call = locationApi.getLocation(location,true,"OR","*:*");
                    Log.v("SEARCH", call.request().toString());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (!response.isSuccessful()) {
                               // Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
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
                           // Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            System.out.println("Something went wrong...");
                        }
                    });
                }
            }
        });
    }


    private void configure_inputText(){
        List<String>allIds=new ArrayList<>();


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://zimbor.go.ro/solr/romania/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LocationRepository locationApi=retrofit.create(LocationRepository.class);



        insert_place.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //allIds.clear();
                Call<JsonObject> call=locationApi.getLocation('\"'+textView.getText().toString()+'\"',true,"OR","*:*");
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                           // Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                            System.out.println("Eroare");
                            return;
                        } else {
                            JsonObject data = response.body();

                            JsonObject responseData = (JsonObject) data.get("response");
                            int numLocations = responseData.getAsJsonArray("docs").size();
                            for(int i=0;i<numLocations;i++){
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String[] ids=String.valueOf(locationData.get("id")).split("\"");
                                if(!allIds.contains(ids[1])) {
                                    allIds.add(ids[1]);
                                }
                                System.out.println("id: "+ids[1]);

                            }

                            System.out.println(allIds);
                            ArrayAdapter<String> placesAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,allIds);
                            insert_place.setAdapter(placesAdapter);

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                       // Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        System.out.println("Something went wrong...");
                    }
                });
                return false;
            }
        });
    }

}
