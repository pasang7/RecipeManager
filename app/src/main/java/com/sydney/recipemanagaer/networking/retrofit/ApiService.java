package com.sydney.recipemanagaer.networking.retrofit;

import com.google.gson.JsonObject;
import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.Review;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Multipart
    @POST("recipe")
    Call<Void> postRecipe(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("ingredients") RequestBody ingredients,
            @Part("instructions") RequestBody instructions,
            @Part("cookingTime") RequestBody cookingTime,
            @Part("createdBy") RequestBody createdBy,
            @Part MultipartBody.Part featuredImage,
            @Part List<MultipartBody.Part> images,
            @Part("category") RequestBody category,
            @Header("Authorization") String token

    );

    @Multipart
    @PATCH("recipe/{id}")
    Call<Void> updateRecipe(
            @Path("id") String recipeId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("ingredients") RequestBody ingredients,
            @Part("instructions") RequestBody instructions,
            @Part("cookingTime") RequestBody cookingTime,
            @Part MultipartBody.Part featuredImage,
            @Part List<MultipartBody.Part> images,
            @Part("category") RequestBody category,
            @Header("Authorization") String token
    );

    @GET("recipe")
    Call<ResponseBody> getRecipes();

    @POST("user/signup")
    Call<ResponseBody> register(
            @Body RequestBody requestBody
    );

    @POST("user/login")
    Call<ResponseBody> login(
            @Body RequestBody requestBody
    );

    @Multipart
    @PATCH("user/updateMe")
    Call<ResponseBody> updateUser(
            @Part("userId") RequestBody userId,
            @Part("username") RequestBody username,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("bio") RequestBody bio,
//            @Part("password") RequestBody password,
            @Part MultipartBody.Part userImage,
            @Part("role") RequestBody role,
            @Header("Authorization") String token
    );

    @GET("recipe/favorites/{userId}")
    Call<ResponseBody> getUserFavorites(@Path("userId") String userId, @Header("Authorization") String token);

    @GET("recipe/myrecipe/{userId}")
    Call<ResponseBody> getUserRecipes(@Path("userId") String userId, @Header("Authorization") String token);

    @DELETE("recipe/{recipeId}")
    Call<String> deleteRecipe(@Path("recipeId") String recipeId, @Header("Authorization") String token);

    @GET("user/{userId}")
    Call<ResponseBody> getUserById(@Path("userId") String userId, @Header("Authorization") String token);

    @PATCH("recipe/{recipeId}/favorite")
    Call<ResponseBody> markRecipeAsFavorite(@Path("recipeId") String recipeId, @Query("userId") String userId, @Header("Authorization") String token);

    @GET("user")
    Call<ResponseBody> getUsers(@Header("Authorization") String token);

    @DELETE("user/{userId}")
    Call<ResponseBody> deleteUser(@Path("userId") String userId, @Header("Authorization") String token);

    @POST("reviews")
    Call<ResponseBody> submitReview(
            @Header("Authorization") String token,
            @Body Review review
    );

    @GET("reviews/{recipeId}")
    Call<ResponseBody> getReviews(
            @Path("recipeId") String recipeId,
            @Header("Authorization") String token
    );

    @GET("categories")
    Call<ResponseBody> getCategories(@Header("Authorization") String token);

    @POST("categories")
    Call<ResponseBody> createCategory(@Body Category category, @Header("Authorization") String token);

    @POST("user/forgetPassword")
    Call<ResponseBody> forgotPassword(@Body JsonObject email);

    @PATCH("user/resetPassword/{token}")
    Call<Void> resetPassword(@Path("token") String token, @Body JsonObject password);

    @PATCH("user/login/updatePassword")
    Call<ResponseBody> updatePassword(
            @Header("Authorization") String token,
            @Body Map<String, String> body
    );
}
