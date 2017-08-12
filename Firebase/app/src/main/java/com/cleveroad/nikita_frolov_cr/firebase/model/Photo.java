package com.cleveroad.nikita_frolov_cr.firebase.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.maps.android.clustering.ClusterItem;

@Table(name = "photo")
public class Photo extends Model implements ClusterItem, Parcelable{
    @Column(name = "photoUri")
    @Expose(deserialize = false, serialize = false)
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

    public Photo() {
    }

    public Photo(Parcel in) {
        photoUri = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        idLink = in.readString();
        link = in.readString();
        title = in.readString();
        snippet = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

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
//        if (!super.equals(o)) return false; TODO question

        Photo photo = (Photo) o;

        if (Double.compare(photo.latitude, latitude) != 0) return false;
        if (Double.compare(photo.longitude, longitude) != 0) return false;
        if (photoUri != null ? !photoUri.equals(photo.photoUri) : photo.photoUri != null)
            return false;
        if (idLink != null ? !idLink.equals(photo.idLink) : photo.idLink != null) return false;
        return link != null ? link.equals(photo.link) : photo.link == null;
    }

    @Override
    public int hashCode() {
//        int result = super.hashCode(); //TODO question
        long temp;
        int result = 1;
        result = 31 * result + (photoUri != null ? photoUri.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (idLink != null ? idLink.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(photoUri);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(idLink);
        parcel.writeString(link);
        parcel.writeString(title);
        parcel.writeString(snippet);
    }
}
