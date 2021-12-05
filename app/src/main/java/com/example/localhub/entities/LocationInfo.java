package com.example.localhub.entities;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class LocationInfo implements Serializable {

    @SerializedName("id")
    UUID id;

    @SerializedName("location_id")
    String locationId;

    @SerializedName("name")
    List<String> locationName;

    @SerializedName("web")
    List<String> web;

    @SerializedName("contact")
    List<String> contact;

    @SerializedName("_version_")
    String version;

    @SerializedName("details")
    List<String> details;

    @SerializedName("social")
    List<String> social;

    @SerializedName("gps")
    List<String> gpsCoordinates;



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public List<String> getLocationName() {
        return locationName;
    }

    public void setLocationName(List<String> locationName) {
        this.locationName = locationName;
    }

    public List<String> getWeb() {
        return web;
    }

    public void setWeb(List<String> web) {
        this.web = web;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getContact() {
        return contact;
    }

    public void setContact(List<String> contact) {
        this.contact = contact;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public List<String> getSocial() {
        return social;
    }

    public void setSocial(List<String> social) {
        this.social = social;
    }

    public List<String> getGpsCoordinates() {
       // List<Double> coordinates =
        return gpsCoordinates;
    }

    public void setGpsCoordinates(List<String> gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }
}
