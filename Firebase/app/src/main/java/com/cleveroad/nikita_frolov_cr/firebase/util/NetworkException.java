package com.cleveroad.nikita_frolov_cr.firebase.util;

public class NetworkException extends Exception {
    public NetworkException(String message) {
        super("NetworkException:" + message);
    }
}
