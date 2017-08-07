package com.cleveroad.nikita_frolov_cr.firebase.repository;

import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;

public interface RemotePhotoRepository {
    Photo uploadPhoto(Photo photo);
}
