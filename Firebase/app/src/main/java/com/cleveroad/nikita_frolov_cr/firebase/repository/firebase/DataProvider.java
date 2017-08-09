package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;


import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoProvider;

public class DataProvider {

    public static PhotoProvider getPhotoProvider() {
        return PhotoProviderLoader.sProvider;
    }

    private static class PhotoProviderLoader {
        private static final PhotoProvider sProvider = new PhotoProviderImpl();
    }

}
