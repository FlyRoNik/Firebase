package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;

import android.net.Uri;

import com.cleveroad.nikita_frolov_cr.firebase.App;
import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.LocalPhotoRepository;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoProvider;

import java.util.List;

public class PhotoProviderImpl implements PhotoProvider {
    private static final Uri PHOTO_UPDATE_URI = Uri.parse("content://"
            + BuildConfig.APPLICATION_ID + ".photoDB/photo");

    private LocalPhotoRepository mLocalPhotoRepository;
    private PhotoNetwork mPhotoNetwork;

    public PhotoProviderImpl() {
        mLocalPhotoRepository = new LocalPhotoRepositoryImpl();
        mPhotoNetwork = new PhotoNetworkImpl();
    }

    @Override
    public List<Photo> getAllPhotos() {
        return mLocalPhotoRepository.getPhotos();
    }

    @Override
    public Photo getPhoto(long id) {
        return mLocalPhotoRepository.getPhoto(id);
    }

    @Override
    public void addPhoto(Photo photo) {
        mLocalPhotoRepository.addPhoto(photo);
        App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
    }

    @Override
    public void removePhoto(long id) {
        mLocalPhotoRepository.removePhoto(id);
        App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
    }


    @Override
    public void uploadPhoto(Photo photo) {
        photo.setLink("loading...");//TODO wtf?
        photo.save();
        App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
        photo = mPhotoNetwork.uploadPhoto(photo);
        photo.save();
        App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
    }


}
