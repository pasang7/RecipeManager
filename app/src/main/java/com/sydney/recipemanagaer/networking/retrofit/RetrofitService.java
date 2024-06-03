package com.sydney.recipemanagaer.networking.retrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.Review;
import com.sydney.recipemanagaer.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitService {
    private final ApiService apiService;

    public RetrofitService(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    private <T> void makeApiCall(Call<T> call, retrofit2.Callback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    callback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.e("RetrofitService", "API call failed: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    private RequestBody createRequestBody(String value) {
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }

    private MultipartBody.Part createMultipartBodyPart(String partName, String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
                return MultipartBody.Part.createFormData(partName, file.getName(), fileBody);
            }
        }
        return null;
    }

    public void getRecipes(Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getRecipes();
        makeApiCall(call, callback);
    }

    public void postRecipe(Recipe recipeData, String userId, String token, Callback<Void> callback) {
        RequestBody titleBody = createRequestBody(recipeData.getTitle());
        RequestBody descriptionBody = createRequestBody(recipeData.getDescription());
        RequestBody ingredientsBody = createRequestBody(new JSONArray(recipeData.getIngredients()).toString());
        RequestBody instructionsBody = createRequestBody(recipeData.getInstructions());
        RequestBody cookingTimeBody = createRequestBody(Integer.toString(recipeData.getCookingTime()));
        RequestBody createdByBody = createRequestBody(userId);
        RequestBody categoryBody = createRequestBody(recipeData.getCategoryId());

        MultipartBody.Part featuredImagePart = createMultipartBodyPart("featuredImage", recipeData.getFeaturedImage());

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (String imagePath : recipeData.getImages()) {
            MultipartBody.Part part = createMultipartBodyPart("images", imagePath);
            if (part != null) imageParts.add(part);
        }

        Call<Void> call = apiService.postRecipe(
                titleBody, descriptionBody, ingredientsBody, instructionsBody,
                cookingTimeBody, createdByBody, featuredImagePart, imageParts, categoryBody, "Bearer " + token
        );

        makeApiCall(call, callback);
    }

    public void updateRecipe(Recipe recipeData, String token, Callback<Void> callback) {
        RequestBody titleBody = createRequestBody(recipeData.getTitle());
        RequestBody descriptionBody = createRequestBody(recipeData.getDescription());
        RequestBody ingredientsBody = createRequestBody(new JSONArray(recipeData.getIngredients()).toString());
        RequestBody instructionsBody = createRequestBody(recipeData.getInstructions());
        RequestBody cookingTimeBody = createRequestBody(Integer.toString(recipeData.getCookingTime()));
        RequestBody categoryBody = createRequestBody(recipeData.getCategoryId());

        MultipartBody.Part featuredImagePart = createMultipartBodyPart("featuredImage", recipeData.getFeaturedImage());

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (String imagePath : recipeData.getImages()) {
            MultipartBody.Part part = createMultipartBodyPart("images", imagePath);
            if (part != null) imageParts.add(part);
        }

        Call<Void> call = apiService.updateRecipe(
                recipeData.getRecipeId(), titleBody, descriptionBody, ingredientsBody,
                instructionsBody, cookingTimeBody, featuredImagePart, imageParts, categoryBody, "Bearer " + token
        );

        makeApiCall(call, callback);
    }

    public void register(User user, Callback<ResponseBody> callback) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", user.getFullName());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("password", user.getPassword());
        jsonObject.put("confirmPassword", user.getConfirmPassword());

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        Call<ResponseBody> call = apiService.register(requestBody);

        makeApiCall(call, callback);
    }

    public void login(String email, String password, Callback<ResponseBody> callback) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        Call<ResponseBody> call = apiService.login(requestBody);

        makeApiCall(call, callback);
    }

    public void getUserFavorites(String userId, String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getUserFavorites(userId, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void getUserRecipes(String userId, String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getUserRecipes(userId, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void deleteRecipe(String recipeId, String token, Callback<String> callback) {
        Call<String> call = apiService.deleteRecipe(recipeId, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void getUserById(String userId, String token, Callback<ResponseBody> callback) {
        if (userId != null) {
            Call<ResponseBody> call = apiService.getUserById(userId, "Bearer " + token);
            makeApiCall(call, callback);
        } else {
            Log.e("NO_USER_ID", "No user id provided");
        }
    }

    public void markRecipeAsFavorite(String recipeId, String userId, String token, Callback<ResponseBody> callback) throws JSONException {
        Call<ResponseBody> call = apiService.markRecipeAsFavorite(recipeId, userId, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void updateUser(User user, String token, Callback<ResponseBody> callback) {
        RequestBody userIdBody = createRequestBody(user.getId());
        RequestBody usernameBody = createRequestBody(user.getUsername());
        RequestBody fullnameBody = createRequestBody(user.getFullName());
        RequestBody emailBody = createRequestBody(user.getEmail());
        RequestBody bioBody = createRequestBody(user.getBio());
        RequestBody roleBody = createRequestBody(user.getRole());

        MultipartBody.Part userImagePart = createMultipartBodyPart("userImage", user.getProfilePic());

        Call<ResponseBody> call = apiService.updateUser(userIdBody, usernameBody, fullnameBody, emailBody, bioBody, userImagePart, roleBody, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void getUsers(String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getUsers("Bearer " + token);
        makeApiCall(call, callback);
    }

    public void deleteUser(String userId, String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.deleteUser(userId, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void submitReview(String token, Review review, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.submitReview("Bearer " + token, review);
        makeApiCall(call, callback);
    }

    public void getReviews(String recipeId, String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getReviews(recipeId, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void getCategories(String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getCategories("Bearer " + token);
        makeApiCall(call, callback);
    }

    public void createCategory(Category category, String token, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.createCategory(category, "Bearer " + token);
        makeApiCall(call, callback);
    }

    public void forgotPassword(String email, Callback<ResponseBody> callback) {
        JsonObject emailJson = new JsonObject();
        emailJson.addProperty("email", email);
        Call<ResponseBody> call = apiService.forgotPassword(emailJson);
        makeApiCall(call, callback);
    }

    public void updatePassword(String token, String currentPassword, String newPassword, String confirmPassword, Callback<ResponseBody> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", currentPassword);
        body.put("password", newPassword);
        body.put("confirmPassword", confirmPassword);

        Call<ResponseBody> call = apiService.updatePassword("Bearer " + token, body);
        makeApiCall(call, callback);
    }
}
