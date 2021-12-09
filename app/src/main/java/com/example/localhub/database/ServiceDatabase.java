/*
package com.example.localhub.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = ServiceEntity.class, exportSchema = false, version = 1)
public abstract class ServiceDatabase extends RoomDatabase {

    private static final String DB_NAME = "Service_DB";
    private static ServiceDatabase instance;


    public static synchronized ServiceDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, ServiceDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ServiceDao serviceDao();
}
*/
