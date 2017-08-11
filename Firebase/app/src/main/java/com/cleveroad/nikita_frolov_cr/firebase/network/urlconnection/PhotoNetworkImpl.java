package com.cleveroad.nikita_frolov_cr.firebase.network.urlconnection;

import android.net.Uri;
import android.text.TextUtils;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.network.PhotoNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoNetworkImpl implements PhotoNetwork {
    private static final String KEY_ID_LINK = "name";
    private static final String MEDIA_LINK = "mediaLink";
    private static final String ALL_PHOTO_URI = "https://fir-fbbcf.firebaseio.com/";
    private static final String BUCKET_URI = "fir-fbbcf.appspot.com";
    private static final String ADD_IMG_BUCKET_URI = "https://www.googleapis.com/upload/storage/v1/b/" +
            BUCKET_URI + "/o?uploadType=media&name=";

    @Override
    public Photo uploadPhoto(Photo photo) throws NetworkException, JSONException, IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ALL_PHOTO_URI + "photo.json");

            String link = uploadImage(photo.getPhotoUri());
            if (!TextUtils.isEmpty(link)) {
                photo.setLink(link);
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                String photoJSON = gson.toJson(photo);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                try (OutputStream outputStream = connection.getOutputStream()) {
                    try (BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(outputStream, "UTF-8"))) {
                        writer.write(photoJSON);
                    }
                }
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream responseStream = connection.getInputStream()) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(responseStream, "UTF-8"))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line).append("\n");
                            }
                            photo.setIdLink(new JSONObject(response.toString()).getString(KEY_ID_LINK));
                            return photo;
                        }
                    }

                } else {
                    throw new NetworkException(String.valueOf(connection.getResponseCode()));
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private String getUrlForAddImg(String imgName) {
        return ADD_IMG_BUCKET_URI + imgName;
    }

    private String uploadImage(Uri uri) throws NetworkException, IOException {
        HttpURLConnection connection = null;
        try {
            String pathImage = uri.getPath();
            String namePhoto = pathImage.substring(pathImage.lastIndexOf('/') + 1);
            URL url = new URL(getUrlForAddImg(namePhoto));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "image/jpeg");
            connection.connect();

            try (DataOutputStream request = new DataOutputStream(connection.getOutputStream())) {
                File file = new File(uri.getPath());
                byte[] bytesArray = new byte[(int) file.length()];

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    while (true) {
                        if (fileInputStream.read(bytesArray) == -1) break;
                    }
                }
                request.write(bytesArray, 0, bytesArray.length);
                request.flush();
            }


            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream responseStream =
                             new BufferedInputStream(connection.getInputStream())) {
                    try (BufferedReader responseStreamReader =
                                 new BufferedReader(new InputStreamReader(responseStream))) {
                        String line;
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((line = responseStreamReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        return new JSONObject(stringBuilder.toString()).getString(MEDIA_LINK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new NetworkException(String.valueOf(connection.getResponseCode()));
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }

}
