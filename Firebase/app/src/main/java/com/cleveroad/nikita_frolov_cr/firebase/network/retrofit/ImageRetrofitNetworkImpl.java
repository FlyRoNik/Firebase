package com.cleveroad.nikita_frolov_cr.firebase.network.retrofit;

import android.net.Uri;

import com.cleveroad.nikita_frolov_cr.firebase.network.ImageNetwork;
import com.cleveroad.nikita_frolov_cr.firebase.util.LOG;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ImageRetrofitNetworkImpl implements ImageNetwork {
    private static final String MEDIA_LINK = "mediaLink";
    private static final String UPLOAD_TYPE = "multipart";
    private static final String BUCKET_URL = "fir-28073.appspot.com";//"fir-fbbcf.appspot.com";

    private GoogleCloudAPIInterface mGoogleCloudAPIInterface;

    public ImageRetrofitNetworkImpl() {
        this.mGoogleCloudAPIInterface = GoogleCloudAPIClient.getClient().create(GoogleCloudAPIInterface.class);
    }

    @Override
    public String uploadImage(Uri uri) throws NetworkException, IOException {

        File file = new File(uri.getPath());

        RequestBody request = null;
        try {
            request = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    new JSONObject().accumulate("name", file.getName()).toString()
            );
        } catch (JSONException e) {
            LOG.e(e);
        }

        MediaType mediaType = MediaType.parse("image/jpeg");

        RequestBody requestFile =
                RequestBody.create(
                        mediaType,
                        file
                );

        MultipartBody.Part metadataPart =
                MultipartBody.Part.create(request);

        MultipartBody.Part mediaPart =
                MultipartBody.Part.create(requestFile);

        Call<ResponseBody> call = mGoogleCloudAPIInterface.upload(metadataPart, mediaPart, BUCKET_URL, UPLOAD_TYPE);
        Response<ResponseBody> response = call.execute();
        if (response.code() == HttpURLConnection.HTTP_OK) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                try {
                    return new JSONObject(responseBody.string()).getString(MEDIA_LINK);
                } catch (JSONException e) {
                    LOG.e(e);
                }
            } else {
                throw new NetworkException("Response body is empty");
            }
        } else {
            throw new NetworkException(String.valueOf(response.message()));
        }
        return "";
    }
}
