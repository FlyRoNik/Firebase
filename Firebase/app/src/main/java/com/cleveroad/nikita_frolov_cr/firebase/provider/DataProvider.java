package com.cleveroad.nikita_frolov_cr.firebase.provider;


public class DataProvider {

    private DataProvider() {
    }

    public static PhotoProvider getPhotoProvider() {
        return PhotoProviderLoader.sProvider;
    }

    private static class PhotoProviderLoader {
        private PhotoProviderLoader() {
        }

        private static final PhotoProvider sProvider = new PhotoProviderImpl();
    }

}
