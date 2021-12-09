/*
package com.example.localhub.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.UUID;

@Dao
public interface ServiceDao {

    @Insert
    void insertService(ServiceEntity service);

    @Query("Delete from Service where service_id = :id")
    void deleteService(UUID id);


    @Query("Select * from Service where service_id = :id")
    ServiceEntity getService(UUID id);

}
*/
