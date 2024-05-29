package com.sydney.recipemanagaer.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sydney.recipemanagaer.model.Category;
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

public class CategoryRepository {
    private final RetrofitService retrofitService;
    private final Context context;
    UserRepository userRepository;

    public CategoryRepository(Context context) {
        this.context = context;
        this.retrofitService = new RetrofitService(context);
        userRepository = new UserRepository(context);
    }

    public LiveData<String> addCategory(Category category) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String token = userRepository.getToken();
        // add cat
        retrofitService.createCategory(category, token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue("Category created successfully.");
                } else {
                    result.setValue("Failed to create category: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue("Error: " + t.getMessage());
            }
        });



        return result;
    }

    public LiveData<List<Category>> getCategories() {
        // get categories

        MutableLiveData<List<Category>> result = new MutableLiveData<>();
        String token = userRepository.getToken();

        retrofitService.getCategories(token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray categoryArray = jsonObject.getJSONArray("categories");
                        List<Category> categories = parseJsonToCategory(categoryArray);
                        result.setValue(categories);
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

    private List<Category> parseJsonToCategory(JSONArray categoryArray) throws JSONException {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject categoryObject = categoryArray.getJSONObject(i);

            // Extract category information
            String id = categoryObject.getString("_id");
            String name = categoryObject.getString("name");
            String desc = categoryObject.optString("description", "");

            Category category = new Category(
                    id,
                    name,
                    desc
            );

            categories.add(category);
        }
        return categories;
    }


}
