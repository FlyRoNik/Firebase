package com.cleveroad.nikita_frolov_cr.firebase.network.retrofit;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface FirebaseAPIInterface {

    @POST("/photo.json")
    Call<ResponseBody> addPhoto(@Body Photo photo);

    @GET("/photo.json")
    Call<ResponseBody> getPhotos();
}
