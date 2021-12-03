package com.example.localhub.domain;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @SerializedName("numFound")
    int numFound;

    @SerializedName("start")
    int start;

    @SerializedName("numFoundExact")
    boolean numFoundExact;

    @SerializedName("docs")
    List<LocationInfo> locationInfoList;

    public List<LocationInfo> getLocationInfoList() {
        return locationInfoList;
    }

    public void setLocationInfoList(List<LocationInfo> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public boolean isNumFoundExact() {
        return numFoundExact;
    }

    public void setNumFoundExact(boolean numFoundExact) {
        this.numFoundExact = numFoundExact;
    }
}
