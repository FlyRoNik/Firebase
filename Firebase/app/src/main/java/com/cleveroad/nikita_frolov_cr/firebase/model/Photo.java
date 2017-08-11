package com.cleveroad.nikita_frolov_cr.firebase.model;

import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.maps.android.clustering.ClusterItem;

@Table(name = "photo")
public class Photo extends Model implements ClusterItem {
    @Column(name = "photoUri")
    @Expose
    private String photoUri;

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

    public Uri getPhotoUri() {
        return Uri.parse(photoUri);
    }

    public void setPhotoUri(Uri photoPath) {
        this.photoUri = photoPath.toString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Photo photo = (Photo) o;

        if (Double.compare(photo.latitude, latitude) != 0) return false;
        if (Double.compare(photo.longitude, longitude) != 0) return false;
        if (photoUri != null ? !photoUri.equals(photo.photoUri) : photo.photoUri != null)
            return false;
        if (idLink != null ? !idLink.equals(photo.idLink) : photo.idLink != null) return false;
        if (link != null ? !link.equals(photo.link) : photo.link != null) return false;
        if (title != null ? !title.equals(photo.title) : photo.title != null) return false;
        return snippet != null ? snippet.equals(photo.snippet) : photo.snippet == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (photoUri != null ? photoUri.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (idLink != null ? idLink.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (snippet != null ? snippet.hashCode() : 0);
        return result;
    }
}
