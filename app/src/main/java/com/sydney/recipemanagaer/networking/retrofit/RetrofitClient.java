package com.sydney.recipemanagaer.networking.retrofit;

import android.content.Context;

import com.sydney.recipemanagaer.utils.Util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = Util.getBaseURL();

    private static Retrofit retrofit;
    private static  OkHttpClient okHttpClient;
    private static Context applicationContext; // Rename to applicationContext for clarity

    public static void clearRetrofitInstance() {
        if (okHttpClient != null) {
            if (okHttpClient.cache() != null) {
                try {
                    okHttpClient.cache().evictAll(); // Clear cache
                } catch (IOException e) {
                    new Throwable(e);
                }
            }
        }
        retrofit = null; // Reset Retrofit instance

    }
    public static Retrofit getRetrofitInstance() {
        HttpLoggingInterceptor interceptRequest = new HttpLoggingInterceptor();
        interceptRequest.setLevel(HttpLoggingInterceptor.Level.BODY);


        if (retrofit == null) {
                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(interceptRequest)
                        .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        applicationContext = context.getApplicationContext();
        return getRetrofitInstance().create(ApiService.class);
    }
}
