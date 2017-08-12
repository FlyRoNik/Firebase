package com.cleveroad.nikita_frolov_cr.firebase.network;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface PhotoNetwork {
    String uploadPhoto(Photo photo) throws NetworkException, JSONException, IOException;

    List<Photo> downloadAllPhotos() throws NetworkException, JSONException, IOException;
}
