package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.LocalPhotoRepository;

import java.util.List;

public class LocalPhotoRepositoryImpl implements LocalPhotoRepository{

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
            for (Photo photo :
                    photos) {
                photo.save();
            }
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
