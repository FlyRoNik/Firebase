package com.cleveroad.nikita_frolov_cr.firebase.provider;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface PhotoProvider {

    List<Photo> getAllPhotos();

    Photo getPhoto(long id);

    void removePhoto(long id);

    void uploadPhoto(Photo photo) throws NetworkException, JSONException, IOException;

    void addPhoto(Photo photo);

    void SyncPhotos() throws IOException, NetworkException, JSONException;
}
