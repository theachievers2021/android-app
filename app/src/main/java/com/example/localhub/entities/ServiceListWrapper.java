package com.example.localhub.entities;

import java.util.List;

public class ServiceListWrapper {
    List<LocationInfo> locationInfoList;

    public List<LocationInfo> getLocationInfoList() {
        return locationInfoList;
    }

    public void setLocationInfoList(List<LocationInfo> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }
}
