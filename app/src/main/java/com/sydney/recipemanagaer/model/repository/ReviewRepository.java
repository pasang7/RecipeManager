package com.sydney.recipemanagaer.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sydney.recipemanagaer.model.Review;
import com.sydney.recipemanagaer.networking.retrofit.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private final RetrofitService retrofitService;
    private final Context context;
    UserRepository userRepository;

    public ReviewRepository(Context context) {
        this.context = context;
        this.retrofitService = new RetrofitService(context);
        userRepository = new UserRepository(context);
    }

    public LiveData<String> submitReview(Review review) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String userId = userRepository.getLoggedInUserId();
        review.setUserId(userId);
        String token = userRepository.getToken();

        retrofitService.submitReview(token, review, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue("Review submitted successfully.");
                } else {
                    result.setValue("Failed to submit review: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue("Error: " + t.getMessage());
            }
        });

        return result;
    }

    public LiveData<List<Review>> getReviews(String recipeId) {
        MutableLiveData<List<Review>> result = new MutableLiveData<>();
        String token = userRepository.getToken();

        retrofitService.getReviews(recipeId, token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray reviewsArray = jsonObject.getJSONArray("data");
                        List<Review> reviews = parseJsonToReviews(reviewsArray);
                        result.setValue(reviews);
                    } catch (IOException | JSONException e) {
                        Log.e("API", "Error parsing response", e);
                        result.setValue(null);
                    }
                } else {
                    Log.e("API", "Error in response: " + response.message());
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "Network error while fetching reviews", t);
                result.setValue(null);
            }
        });

        return result;
    }

    private List<Review> parseJsonToReviews(JSONArray jsonArray) throws JSONException {
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // Extract recipe information
            JSONObject recipeObject = jsonObject.getJSONObject("recipe");
            String recipeId = recipeObject.getString("_id");
            // extract user info
            JSONObject userObject = jsonObject.getJSONObject("user");
            String username = userObject.getString("username");
            float rating = 0;
            if (jsonObject.has("rating")) {
                rating = (float) jsonObject.getDouble("rating");
            }
            String reviewText = "";
            if(jsonObject.has("review")) {
                reviewText = jsonObject.getString("review");
            }
            Review review = new Review(
                    reviewText,
                    rating,
                    recipeId,
                    username
            );

            reviews.add(review);
        }
        return reviews;
    }

}
