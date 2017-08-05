package com.cleveroad.nikita_frolov_cr.firebase.repository;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

import java.util.List;

public interface LocalPhotoRepository {

    void savePhoto(Photo photo);

    Photo getPhoto(int id);

    void savePhotos(List<Photo> photos);

    List<Photo> getPhotos();
}
