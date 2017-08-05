package com.cleveroad.nikita_frolov_cr.firebase.repository;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

public interface RemotePhotoRepository {
    String savePhoto(Photo photo);

    Photo getPhoto(String key);
}
