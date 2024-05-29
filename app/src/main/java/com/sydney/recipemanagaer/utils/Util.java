package com.sydney.recipemanagaer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bumptech.glide.Glide;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.ui.view.activities.LoginActivity;
import com.sydney.recipemanagaer.ui.view.activities.MainActivity;
import com.sydney.recipemanagaer.ui.view.fragments.RecipeDetailFragment;
import com.sydney.recipemanagaer.ui.view.fragments.UpdateRecipeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class Util {
    public static final String SHARED_PREFS_FILE = "appPreferences";
    public static final String TOKEN_KEY = "sessionToken";
    public static final String USER_ID_KEY = "userID";

    public static void handleViewRecipeDetail(Recipe recipe, Activity activity) {
        // Ensure the activity is a FragmentActivity before attempting to use getSupportFragmentManager
        if (activity instanceof FragmentActivity) {
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            Bundle args = new Bundle();
            args.putString("recipeId", recipe.getRecipeId());
            args.putString("title", recipe.getTitle());
            args.putString("description", recipe.getDescription());
            args.putString("ingredients", String.join(", ", recipe.getIngredients())); // Joining the list of ingredients into a single string
            args.putString("imageUrl", recipe.getFeaturedImgURL());
            args.putString("category", recipe.getCategoryId());
            args.putStringArrayList("imagesUrl", (ArrayList<String>) recipe.getImages());
            args.putString("instructions", recipe.getInstructions());
            args.putString("cookingTime", recipe.getCookingTime() + ""); // Pass as int if you want to use it as a number later
            detailFragment.setArguments(args);

            // Perform the fragment transaction to display the RecipeDetailFragment
            ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            // Log or handle the case where the activity is not a FragmentActivity
            System.out.println("Error: Activity provided is not a FragmentActivity and cannot perform fragment transactions.");
        }
    }

    public static void loadImage(UpdateRecipeFragment context, String imageUrl, ImageView imageViewSelected) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image_background)
                    .error(R.drawable.error_image)
                    .into(imageViewSelected);
        }
    }

    // Static method to check if user is logged in
    public static boolean userIsLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        // Return false if no token is available
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            // Use a more specific exception type instead of general Exception
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expiration = decodedJWT.getExpiresAt();

            // Return true if the token is not expired, false otherwise
            return expiration != null && expiration.after(new Date());
        } catch (JWTDecodeException e) {
            // Log the exception with a more descriptive message
            Log.e("UserCheck", "Error decoding JWT: " + e.getMessage(), e);
            return false;
        }
    }

    // Static method to navigate to the login activity
    public static void navigateToLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
        context.startActivity(intent);
    }


    public static String getPath(Context context, Uri uri) {
        if (uri == null) {
            Log.e("ImagePath", "URI is null");
            return null;
        }

        Log.i("ImagePath", "URI: " + uri.toString());
        String filePath = null;

        // Try to retrieve the file path by copying content to a temp file
        try {
            filePath = copyContentToTempFile(context, uri);
        } catch (Exception e) {
            Log.e("ImagePath", "Error getting full file path", e);
        }

        Log.i("ImagePath", "Full File Path: " + filePath);
        return filePath;
    }

    // Copy content to a temporary file to get its path
    private static String copyContentToTempFile(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        String displayName = null;

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                displayName = cursor.getString(nameIndex);
            }
            cursor.close();
        }

        if (displayName == null) {
            Log.e("ImagePath", "Unable to retrieve file name");
            return null;
        }

        File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), displayName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            Log.e("ImagePath", "Error copying file to temp location", e);
            return null;
        }

        return tempFile.getAbsolutePath();
    }

    public static String getBaseURL() {
        return "http://10.0.2.2:8000/api/v1/";
    }

    public static void navigateToMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
        context.startActivity(intent);
    }
}
