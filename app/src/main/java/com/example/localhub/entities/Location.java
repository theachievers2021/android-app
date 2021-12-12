package com.example.localhub.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("localitate")
    @Expose
    private String localitate;

    @SerializedName("judet")
    @Expose
    private String judet;

    @SerializedName("_version_")
    @Expose
    private Long version;


    public Location(String id, String localitate, String judet, Long version){
        this.id=id;
        this.localitate=localitate;
        this.judet=judet;
        this.version=version;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getLocalitate() {
        return localitate;
    }

    public void setLocalitate(String localitate) {
        this.localitate = localitate;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

}
