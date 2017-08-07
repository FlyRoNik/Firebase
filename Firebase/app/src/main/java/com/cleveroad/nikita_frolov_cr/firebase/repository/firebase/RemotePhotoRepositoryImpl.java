package com.cleveroad.nikita_frolov_cr.firebase.repository.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.RemotePhotoRepository;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class RemotePhotoRepositoryImpl implements RemotePhotoRepository {
    private static final String KEY_ID_LINK = "name";
    private static final String MEDIA_LINK = "mediaLink";
    private static final String ALL_PHOTO_URI = "https://fir-fbbcf.firebaseio.com/";
    private static final String BUCKET_URI = "fir-fbbcf.appspot.com";
    private static final String ADD_IMG_BUCKET_URI = "https://www.googleapis.com/upload/storage/v1/b/" +
            BUCKET_URI + "/o?uploadType=media&name=";

    @Override
    public Photo uploadPhoto(Photo photo) {
        HttpURLConnection connection = null;
        try{
            URL url = new URL(ALL_PHOTO_URI + "photo.json");

            String link = uploadImage(photo.getPhotoPath());

            if (!Objects.equals(link, "")) {
                photo.setLink(link);

                Gson gson = new Gson();
                String photoJSON = gson.toJsonTree(photo).getAsString();

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                try(OutputStream outputStream = connection.getOutputStream()){
                    try(BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(outputStream, "UTF-8"))) {
                        writer.write(photoJSON);
                    }
                }
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream responseStream = connection.getInputStream()) {
                        try(BufferedReader reader = new BufferedReader(
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
                    //TODO wtf??
                    throw new IOException(String.valueOf(connection.getResponseCode()));
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private String getUrlForAddImg(String imgName){
        return ADD_IMG_BUCKET_URI + "/o?uploadType=media&name=" + imgName;
    }

    private Bitmap getImageFromPath(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return  BitmapFactory.decodeFile(imagePath, options);
    }

    private String uploadImage(String pathImage){
        HttpURLConnection connection = null;
        try{
            Bitmap photo = getImageFromPath(pathImage);
            String namePhoto = pathImage.substring(pathImage.lastIndexOf("/")+1);
            URL url = new URL(getUrlForAddImg(namePhoto));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "image/jpeg");
            connection.connect();

            try(DataOutputStream request = new DataOutputStream(connection.getOutputStream())) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                request.write(stream.toByteArray());
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
                //TODO wtf??
                throw new IOException(String.valueOf(connection.getResponseCode()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }

}
