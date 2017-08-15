package com.cleveroad.nikita_frolov_cr.firebase.network.retrofit;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface GoogleCloudAPIInterface {
    @Multipart
    @POST("upload/storage/v1/b/{nameBucket}/o")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part metaPart,
            @Part MultipartBody.Part dataPart,
            @Path("nameBucket") String nameBucket,
            @Query("uploadType") String uploadType);
}
