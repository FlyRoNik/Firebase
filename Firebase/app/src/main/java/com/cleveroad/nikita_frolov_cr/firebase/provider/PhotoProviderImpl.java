package com.cleveroad.nikita_frolov_cr.firebase.provider;

import android.net.Uri;
import android.text.TextUtils;

import com.cleveroad.nikita_frolov_cr.firebase.App;
import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.network.ImageNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.network.urlconnection.ImageNetworkImpl;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoRepository;
import com.cleveroad.nikita_frolov_cr.firebase.network.PhotoNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoRepositoryImpl;
import com.cleveroad.nikita_frolov_cr.firebase.network.urlconnection.PhotoNetworkImpl;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class PhotoProviderImpl implements PhotoProvider {
    private static final Uri PHOTO_UPDATE_URI = Uri.parse("content://"
            + BuildConfig.APPLICATION_ID + ".photoDB/photo");

    private PhotoRepository mPhotoRepository;
    private PhotoNetwork mPhotoNetwork;
    private ImageNetwork mImageNetwork;

    public PhotoProviderImpl() {
        mPhotoRepository = new PhotoRepositoryImpl();
        mPhotoNetwork = new PhotoNetworkImpl();
        mImageNetwork = new ImageNetworkImpl();
    }

    @Override
    public List<Photo> getAllPhotos() {
        return mPhotoRepository.getPhotos();
    }

    @Override
    public Photo getPhoto(long id) {
        return mPhotoRepository.getPhoto(id);
    }

    @Override
    public void addPhoto(Photo photo) {
        mPhotoRepository.addPhoto(photo);
        App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
    }

    @Override
    public void removePhoto(long id) {
        mPhotoRepository.removePhoto(id);
        App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
    }


    @Override
    public void uploadPhoto(Photo photo) throws NetworkException, JSONException, IOException {
        String link = mImageNetwork.uploadImage(photo.getPhotoUri());
        if (!TextUtils.isEmpty(link)) {
            photo.setLink(link);
            String idLink = mPhotoNetwork.uploadPhoto(photo);
            photo.setIdLink(idLink);
            photo.save();
            App.get().getContentResolver().notifyChange(PHOTO_UPDATE_URI, null);
        }
    }


}
