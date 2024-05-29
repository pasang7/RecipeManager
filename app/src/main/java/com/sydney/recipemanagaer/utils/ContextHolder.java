package com.sydney.recipemanagaer.utils;

import android.content.Context;

public class ContextHolder {
    private static Context context;


    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static Context getContext() {
        return context;
    }
}