package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.RemotePhotoRepository;
import com.google.gson.Gson;

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

public class RemotePhotoRepositoryImpl implements RemotePhotoRepository {

    private static final String ALL_PHOTO_URI = "https://fir-fbbcf.firebaseio.com/";

    @Override
    public String savePhoto(Photo photo) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            URL url = new URL(ALL_PHOTO_URI + "photo.json");

            //TODO save photo to google claude and return Link
            JSONObject link = new JSONObject();
            link.accumulate("link", "TODO set link photo");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(link.toString());
            writer.close();
            outputStream.close();

            InputStream is;
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            is.close();

            return new JSONObject(response.toString()).getString("name");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Photo getPhoto(String key) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(ALL_PHOTO_URI + "photo/" + key + ".json");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }

            JSONObject finalObject = new JSONObject(buffer.toString());
            Photo photo = new Gson().fromJson(finalObject.toString(), Photo.class);

            //TODO something photo link

            return photo;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }


}
