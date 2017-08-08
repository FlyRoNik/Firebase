package com.cleveroad.nikita_frolov_cr.firebase.repository;

import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;

import java.util.List;

public interface PhotoProvider {

    List<Photo> getAllPhotos();

    Photo getPhoto(long id);

    void removePhoto(long id);

    void uploadPhoto(Photo photo);

    void addPhoto(Photo photo);
}
