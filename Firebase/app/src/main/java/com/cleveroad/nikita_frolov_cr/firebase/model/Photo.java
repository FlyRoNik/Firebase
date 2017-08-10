package com.cleveroad.nikita_frolov_cr.firebase.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.maps.android.clustering.ClusterItem;

@Table(name = "photo")
public class Photo extends Model implements ClusterItem {
    @Column(name = "photoPath")
    @Expose
    private String photoPath;

    @Column(name = "latitude")
    @Expose
    private double latitude;

    @Column(name = "longitude")
    @Expose
    private double longitude;

    @Column(name = "idLink")
    @Expose(deserialize = false, serialize = false)
    private String idLink;

    @Column(name = "link")
    @Expose
    private String link;

    @Expose(deserialize = false, serialize = false)
    private String title;

    @Expose(deserialize = false, serialize = false)
    private String snippet;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public LatLng getLatitude() {
        return new LatLng(latitude, longitude);
    }

    public void setLatitude(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public String getIdLink() {
        return idLink;
    }

    public void setIdLink(String idLink) {
        this.idLink = idLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
