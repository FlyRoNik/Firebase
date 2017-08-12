package com.cleveroad.nikita_frolov_cr.firebase.network;

import android.net.Uri;

import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;

import java.io.IOException;

public interface ImageNetwork {
    String uploadImage(Uri uri) throws NetworkException, IOException;
}
