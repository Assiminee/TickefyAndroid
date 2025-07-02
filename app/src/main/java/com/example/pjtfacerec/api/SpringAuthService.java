package com.example.pjtfacerec.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SpringAuthService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginRequestBody body);
}
