package com.cleveroad.nikita_frolov_cr.firebase.util;

import android.util.Log;

import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;

public class LOG {
    private LOG() {
    }

    public static void e(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("FirebaseException: ", throwable.getMessage());
        }
    }

    public static void d(String str) {
        if (BuildConfig.DEBUG) {
            Log.d("FirebaseDebug: ", str);
        }
    }
}
