package com.cleveroad.nikita_frolov_cr.firebase.repository;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

import java.util.List;

public class PhotoRepositoryImpl implements PhotoRepository {

    @Override
    public Long addPhoto(Photo photo) {
        return photo.save();
    }

    @Override
    public Photo getPhoto(long id) {
        return Photo.load(Photo.class, id);
    }

    @Override
    public void addPhotos(List<Photo> photos) {
        ActiveAndroid.beginTransaction();
        try {
            photos.forEach(Model::save);
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    @Override
    public List<Photo> getPhotos() {
        return new Select().from(Photo.class).execute();
    }

    @Override
    public void removePhoto(long id) {
        Photo photo = Photo.load(Photo.class, id);
        photo.delete();
    }
}
