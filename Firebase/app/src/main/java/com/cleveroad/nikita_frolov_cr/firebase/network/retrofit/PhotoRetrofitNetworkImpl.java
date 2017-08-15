package com.cleveroad.nikita_frolov_cr.firebase.network.retrofit;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.network.PhotoNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PhotoRetrofitNetworkImpl implements PhotoNetwork{
    private static final String KEY_ID_LINK = "name";
    private FirebaseAPIInterface mFirebaseAPIInterface;

    public PhotoRetrofitNetworkImpl() {
        mFirebaseAPIInterface = FirebaseAPIClient.getClient().create(FirebaseAPIInterface.class);
    }

    @Override
    public String uploadPhoto(Photo photo) throws NetworkException, JSONException, IOException {
        Call<ResponseBody> call = mFirebaseAPIInterface.addPhoto(photo);
        Response<ResponseBody> response = call.execute();
        if (response.code() == HttpURLConnection.HTTP_OK) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return new JSONObject(responseBody.string()).getString(KEY_ID_LINK);
            }else {
                throw new NetworkException("Response body is empty");
            }
        } else {
            throw new NetworkException(String.valueOf(response.message()));
        }
    }

    @Override
    public List<Photo> downloadAllPhotos() throws NetworkException, JSONException, IOException {
        Call<ResponseBody> call = mFirebaseAPIInterface.getPhotos();
        Response<ResponseBody> response = call.execute();
        if (response.code() == HttpURLConnection.HTTP_OK) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                List<Photo> photos = new ArrayList<>();
                String s = responseBody.string();
                JSONObject photosJson = new JSONObject(s);
                Iterator<String> keys = photosJson.keys();
                Gson gson = new Gson();
                while(keys.hasNext()) {
                    String key = keys.next();
                    JSONObject photoJson = photosJson.getJSONObject(key);
                    Photo photo = gson.fromJson(photoJson.toString(), Photo.class);
                    photo.setIdLink(key);
                    photos.add(photo);
                }
                return photos;
            }else {
                throw new NetworkException("Response body is empty");
            }
        } else {
            throw new NetworkException(String.valueOf(response.message()));
        }
    }
}
