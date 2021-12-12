package com.example.localhub.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String FAVORITES_TABLE = "FAVORITES_TABLE";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_LOCATION_ID = "LOC_ID";
    private static final String COLUMN_LOCATION_NAME = "LOC_NAME";
    private static final String COLUMN_LOCATION_WEB = "WEB";
    private static final String COLUMN_CONTACT = " CONTACT";
    private static final String COLUMN_DETAILS = " DETAILS";
    private static final String COLUMN_SOCIALS = " SOCIAL";
    private static final String COLUMN_GPS = "GPS";

    private static  final int DATABASE_VERSION = 11;
    private static  final String DATABASE_NAME = "favorites.db";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement =
                "CREATE TABLE " + FAVORITES_TABLE + " (" +
                        COLUMN_ID +  " INTEGER PRIMARY KEY, " +
                        COLUMN_LOCATION_ID + " TEXT, " +
                        COLUMN_LOCATION_NAME + " TEXT, "+
                        COLUMN_LOCATION_WEB + " TEXT, " +
                        COLUMN_CONTACT + " TEXT, " +
                        COLUMN_DETAILS + " TEXT, " +
                        COLUMN_SOCIALS + " TEXT, " +
                        COLUMN_GPS + " TEXT "+
                        " ) ";

        db.execSQL(createTableStatement);

    }


    public boolean addOneString(String locationId, String locationName, String locationWeb, String locationContact, String locationDetails, String locationSocial, String gps){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LOCATION_ID, locationId);
        cv.put(COLUMN_LOCATION_NAME, locationName);
        cv.put(COLUMN_LOCATION_WEB, locationWeb);
        cv.put(COLUMN_CONTACT, locationContact);
        cv.put(COLUMN_DETAILS, locationDetails);
        cv.put(COLUMN_SOCIALS, locationSocial);
        cv.put(COLUMN_GPS, gps);

        long insert = db.insert(FAVORITES_TABLE, null, cv);
        db.close();

        if(insert == -1){
            return false;
        }else{
            return true;
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);


        onCreate(db);
    }

    public boolean deleteOne(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + FAVORITES_TABLE + " WHERE " + COLUMN_ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            return true;

        }else{
            return false;
        }
    }

    public boolean deleteOneName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + FAVORITES_TABLE + " WHERE " + COLUMN_LOCATION_NAME + " = " + name;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            return true;

        }else{
            return false;
        }
    }

    public List<Integer> getId(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<Integer> locId = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                locId.add(id);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return locId;
    }


    public List<String> getLocationId(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> locId = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(1);
                locId.add(id);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return locId;
    }


    public List<String> getLocationName(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> locName = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(2);
                locName.add(name);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return locName;
    }

    public List<String> getLocationWeb(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> webs = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String w = cursor.getString(3);
                webs.add(w);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return webs;
    }

    public List<String> getLocationContact(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> contacts = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String cont = cursor.getString(4);
                contacts.add(cont);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contacts;
    }

    public List<String> getLocationDetails(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> details = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String d = cursor.getString(5);
                details.add(d);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return details;
    }

    public List<String> getLocationSocials(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> socials = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String s = cursor.getString(6);
                socials.add(s);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return socials;
    }

    public List<String> getLocationGps(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITES_TABLE;
        List<String> gps = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String g = cursor.getString(7);
                gps.add(g);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return gps;
    }
}
