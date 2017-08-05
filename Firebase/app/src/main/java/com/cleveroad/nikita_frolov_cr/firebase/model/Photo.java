package com.cleveroad.nikita_frolov_cr.firebase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

@Table(name = "photo")
public class Photo extends Model{
    @Expose(serialize = false, deserialize = false)
    private int resource;

    @Expose(serialize = false, deserialize = false)
    private double latitude;

    @Expose(serialize = false, deserialize = false)
    private double longitude;

    @Expose(serialize = false, deserialize = false)
    private boolean load;

    @Expose
    private String idLink;

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public LatLng getLatitude() {
        return new LatLng(latitude, longitude);
    }

    public void setLatitude(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public String getIdLink() {
        return idLink;
    }

    public void setIdLink(String idLink) {
        this.idLink = idLink;
    }
}
