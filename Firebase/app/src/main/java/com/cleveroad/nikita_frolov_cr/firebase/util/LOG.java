package com.cleveroad.nikita_frolov_cr.firebase.util;

import android.util.Log;

import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;

class LOG {
    private LOG() {
    }

    static void e(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("sds", throwable.getMessage());
        }
    }

    public static void d(String str) {
        if (BuildConfig.DEBUG) {
            Log.d("sds", str);
        }
    }
}
