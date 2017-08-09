package com.cleveroad.nikita_frolov_cr.firebase;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class App extends Application {
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        ActiveAndroid.initialize(this);
    }

    public static Application get() {
        return sInstance;
    }
}
