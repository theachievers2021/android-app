package com.example.localhub.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class LocationInfo {

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
}
