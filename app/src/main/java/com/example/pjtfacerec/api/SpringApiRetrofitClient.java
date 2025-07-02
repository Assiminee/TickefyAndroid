package com.example.pjtfacerec.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpringApiRetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            String BASE_URL = "http://10.122.104.241:5001/api/users/";
            SharedPreferencesService service = new SharedPreferencesService(context);
            String token = service.getToken();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        return chain.proceed(request
                                .newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build()
                        );
                    })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
