package com.sydney.recipemanagaer.networking.retrofit;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.sydney.recipemanagaer.utils.Util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class AuthInterceptor implements Interceptor {
    private String token;
    private static SharedPreferences sharedPreferences;

    public AuthInterceptor(Context context) {
        sharedPreferences = context.getSharedPreferences(Util.SHARED_PREFS_FILE, MODE_PRIVATE);
        token = sharedPreferences.getString(Util.TOKEN_KEY, null);
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (token != null && !token.isEmpty()) {

            Request newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }
        return chain.proceed(request);
    }
}
