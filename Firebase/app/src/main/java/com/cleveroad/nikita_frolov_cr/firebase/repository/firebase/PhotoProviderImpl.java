package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;

import android.content.ContentResolver;
import android.net.Uri;

import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;
import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.LocalPhotoRepository;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoProvider;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoNetwork;

import java.util.List;

public class PhotoProviderImpl implements PhotoProvider {
    private static final Uri PHOTO_UPDATE_URI = Uri.parse("content://"
            + BuildConfig.APPLICATION_ID + ".photoDB/photo");

    private LocalPhotoRepository mLocalPhotoRepository;
    private PhotoNetwork mPhotoNetwork;
    private ContentResolver mContentResolver;

    public PhotoProviderImpl(ContentResolver contentResolver) {
        mLocalPhotoRepository = new LocalPhotoRepositoryImpl();
        mPhotoNetwork = new PhotoNetworkImpl();
        mContentResolver = contentResolver;
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
        mContentResolver.notifyChange(PHOTO_UPDATE_URI, null);
    }

    @Override
    public void removePhoto(long id) {
        mLocalPhotoRepository.removePhoto(id);
        mContentResolver.notifyChange(PHOTO_UPDATE_URI, null);
    }


    @Override
    public void uploadPhoto(Photo photo) {
        photo = mPhotoNetwork.uploadPhoto(photo);
        photo.save();
    }


}
