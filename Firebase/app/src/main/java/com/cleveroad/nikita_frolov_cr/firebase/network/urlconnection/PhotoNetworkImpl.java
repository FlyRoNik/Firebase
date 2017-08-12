package com.cleveroad.nikita_frolov_cr.firebase.network.urlconnection;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.network.PhotoNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoNetworkImpl implements PhotoNetwork {
    private static final String KEY_ID_LINK = "name";
    private static final String ALL_PHOTO_URI = "https://fir-fbbcf.firebaseio.com/";

    @Override
    public String uploadPhoto(Photo photo) throws NetworkException, JSONException, IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ALL_PHOTO_URI + "photo.json");

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
                        return new JSONObject(response.toString()).getString(KEY_ID_LINK);
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
    }
}
