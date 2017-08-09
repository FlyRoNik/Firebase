package com.cleveroad.nikita_frolov_cr.firebase.repository;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

import java.util.List;

public interface LocalPhotoRepository {

    Long addPhoto(Photo photo);

    Photo getPhoto(long id);

    void addPhotos(List<Photo> photos);

    List<Photo> getPhotos();

    void removePhoto(long id);
}
