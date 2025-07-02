package com.example.pjtfacerec.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SpringApiService {
    @Multipart
    @POST("identify")
    Call<SpringResponse> identify(@Part() MultipartBody.Part imageFile);
}
