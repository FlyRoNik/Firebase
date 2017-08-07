package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;

import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.LocalPhotoRepository;
import com.cleveroad.nikita_frolov_cr.firebase.repository.Provider;
import com.cleveroad.nikita_frolov_cr.firebase.repository.RemotePhotoRepository;

import java.util.List;

public class ProviderImpl implements Provider {
    private LocalPhotoRepository mLocalPhotoRepository;
    private RemotePhotoRepository mRemotePhotoRepository;

    public ProviderImpl(LocalPhotoRepository mLocalPhotoRepository, RemotePhotoRepository mRemotePhotoRepository) {
        this.mLocalPhotoRepository = mLocalPhotoRepository;
        this.mRemotePhotoRepository = mRemotePhotoRepository;
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
        //TODO change data
    }

    @Override
    public void removePhoto(long id) {
        mLocalPhotoRepository.removePhoto(id);
        //TODO change data
    }


    @Override
    public void uploadPhoto(Photo photo) {
        photo = mRemotePhotoRepository.uploadPhoto(photo);
        photo.save();
        //TODO change data
    }


}
