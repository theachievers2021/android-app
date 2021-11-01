package com.lsuciu.social_impact;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveGoogleMapsCityName extends AsyncTask<URL, Integer, Void> {
    protected AppCompatActivity a;
    protected AlertDialog dialog;
    String cityName;
    String stateLetters;

    private RetrieveGoogleMapsCityName(AppCompatActivity a) {
        this.a = a;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = new AlertDialog.Builder(this.a).create();
        this.dialog.setCancelable(false);
        this.dialog.setMessage("*****");
        this.dialog.setTitle("*****");
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(URL... urls) {
        try {
            final HttpURLConnection conn;
            conn = (HttpURLConnection) urls[0].openConnection();
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),
                    "UTF-8"), 8);

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            if (!sb.toString().isEmpty()) {
                JSONObject obj = new JSONObject(sb.toString());
                JSONArray results = obj.getJSONArray("results");
                JSONArray address_components = results.getJSONObject(0).getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    //Log.v("JSONArray", address_components.getJSONObject(i).getString("long_name"));
                    //Log.v("JSONArrayTypes", address_components.getJSONObject(i).getJSONArray("types").getString(0));

                    if (address_components.getJSONObject(i).getJSONArray("types").getString(0).contentEquals("locality")) {
                        Log.v("JSONArray:i", i + " " + address_components.getJSONObject(i).getString("long_name"));
                        cityName = address_components.getJSONObject(i).getString("long_name");
                    }
                    if (address_components.getJSONObject(i).getJSONArray("types").getString(0).contentEquals("administrative_area_level_1")) {
                        stateLetters = address_components.getJSONObject(i).getString("short_name");
                    }
                }
                /*cityName =
                        obj.getJSONArray("results").getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(1).getString("long_name");
                stateLetters = obj.getJSONArray("results").getJSONObject(0)
                        .getJSONArray("address_components").getJSONObject(4).getString("short_name");*/
            }
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        this.dialog.dismiss();
       // hasLocation = true;
        Log.v("onPostExecute", "You are located in " + cityName + ", " + stateLetters);
    }
}