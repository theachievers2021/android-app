package com.example.retrofit.feature_get_location.domain.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.retrofit.R;
import com.example.retrofit.feature_get_location.domain.model.Location;
import com.example.retrofit.feature_get_location.domain.model.LocationModel;
import com.example.retrofit.feature_get_location.domain.repository.LocationRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    TextView textShow;
    TextInputEditText textCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textShow=findViewById(R.id.text_show);
        textCity=findViewById(R.id.text_city);

        List<String>allIds=new ArrayList<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://zimbor.go.ro/solr/romania/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        LocationRepository locationApi=retrofit.create(LocationRepository.class);

        textCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                allIds.clear();
                Call<JsonObject>  call=locationApi.getLocation(textView.getText().toString(),true,"OR","*:*");
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            textShow.setText("Code: " + response.code());
                            return;
                        } else {
                            JsonObject data = response.body();

                            JsonObject responseData = (JsonObject) data.get("response");
                            int numLocations = responseData.getAsJsonArray("docs").size();
                            for(int i=0;i<numLocations;i++){
                                JsonObject locationData = (JsonObject) responseData.getAsJsonArray("docs").get(i);
                                String id=String.valueOf(locationData.get("id"));
                                allIds.add(id);

                            }



                            textShow.setText(allIds.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        textShow.setText(t.getMessage());
                    }
                });
                return false;
            }
        });


    }
}