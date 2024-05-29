package com.sydney.recipemanagaer.networking.retrofit;

import android.content.Context;
import android.util.Log;

import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.Review;
import com.sydney.recipemanagaer.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public void getRecipes(Callback<ResponseBody> retrofitCallback) {
        // Make API call
        Call<ResponseBody> call = apiService.getRecipes();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("RetrofitService", "Recipes fetched successfully.");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Failed to fetch recipes: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void postRecipe(Recipe recipeData, String userId, retrofit2.Callback<Void> retrofitCallback) {
        // Prepare text parameters
        RequestBody titleBody = RequestBody.create(recipeData.getTitle(), MediaType.parse("text/plain"));
        RequestBody descriptionBody = RequestBody.create(recipeData.getDescription(), MediaType.parse("text/plain"));
        JSONArray jsonArray = new JSONArray(recipeData.getIngredients());
        RequestBody ingredientsBody = RequestBody.create(jsonArray.toString(), MediaType.parse("application/json"));
        RequestBody instructionsBody = RequestBody.create(recipeData.getInstructions(), MediaType.parse("text/plain"));
        RequestBody cookingTimeBody = RequestBody.create(Integer.toString(recipeData.getCookingTime()), MediaType.parse("text/plain"));
        RequestBody createdByBody = RequestBody.create(userId, MediaType.parse("text/plain"));
        RequestBody categoryBody = RequestBody.create(recipeData.getCategoryId(), MediaType.parse("text/plain"));


        // Prepare file parameters
        MultipartBody.Part featuredImagePart = null;
        File featuredImageFile = new File(recipeData.getFeaturedImage());
        if (featuredImageFile.exists()) {
            RequestBody fileBody = RequestBody.create(featuredImageFile, MediaType.parse("image/*"));
            featuredImagePart = MultipartBody.Part.createFormData("featuredImage", featuredImageFile.getName(), fileBody);
        }

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (int i = 0; i < recipeData.getImages().size(); i++) {
            File file = new File(recipeData.getImages().get(i));
            if (file.exists()) {
                RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
                MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
                imageParts.add(part);
            }
        }

        // Make API call
        Call<Void> call = apiService.postRecipe(
                titleBody, descriptionBody, ingredientsBody, instructionsBody,
                cookingTimeBody, createdByBody, featuredImagePart, imageParts, categoryBody
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("RetrofitService", "Recipe posted successfully.");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RetrofitService", "Failed to post recipe: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void updateRecipe(Recipe recipeData, retrofit2.Callback<Void> retrofitCallback) {
        // Prepare text parameters
        RequestBody titleBody = RequestBody.create(recipeData.getTitle(), MediaType.parse("text/plain"));
        RequestBody descriptionBody = RequestBody.create(recipeData.getDescription(), MediaType.parse("text/plain"));
        RequestBody ingredientsBody = RequestBody.create(new JSONArray(recipeData.getIngredients()).toString(), MediaType.parse("application/json"));
        RequestBody instructionsBody = RequestBody.create(recipeData.getInstructions(), MediaType.parse("text/plain"));
        RequestBody cookingTimeBody = RequestBody.create(Integer.toString(recipeData.getCookingTime()), MediaType.parse("text/plain"));
        RequestBody categoryBody = RequestBody.create(recipeData.getCategoryId(), MediaType.parse("text/plain"));

        // Prepare file parameters (Handle null or empty paths)
        MultipartBody.Part featuredImagePart = null;
        String featuredImagePath = recipeData.getFeaturedImage();
        if (featuredImagePath != null && !featuredImagePath.isEmpty()) {
            File featuredImageFile = new File(featuredImagePath);
            if (featuredImageFile.exists()) {
                RequestBody fileBody = RequestBody.create(featuredImageFile, MediaType.parse("image/*"));
                featuredImagePart = MultipartBody.Part.createFormData("featuredImage", featuredImageFile.getName(), fileBody);
            }
        }

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (String imagePath : recipeData.getImages()) {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
                    imageParts.add(part);
                }
            }
        }

        // Make API call
        Call<Void> call = apiService.updateRecipe(
                recipeData.getRecipeId(), titleBody, descriptionBody, ingredientsBody,
                instructionsBody, cookingTimeBody, featuredImagePart, imageParts, categoryBody
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void register(User user, retrofit2.Callback<ResponseBody> retrofitCallback) throws JSONException {
        // Prepare text parameters
//        RequestBody fullnameBody = RequestBody.create(user.getFullName(), MediaType.parse("text/plain"));
//        RequestBody emailBody = RequestBody.create(user.getEmail(), MediaType.parse("text/plain"));
//        RequestBody passwordBody = RequestBody.create(user.getPassword(), MediaType.parse("text/plain"));
//        RequestBody confirmPassword = RequestBody.create(user.getConfirmPassword(), MediaType.parse("text/plain"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", user.getFullName());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("password", user.getPassword());
        jsonObject.put("confirmPassword", user.getConfirmPassword());


        // Convert the JSONObject to a RequestBody
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        // Make API call
        Call<ResponseBody> call = apiService.register(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("RetrofitService", "User registered successfully.");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Failed to register user: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void login(String email, String password,  retrofit2.Callback<ResponseBody> retrofitCallback) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        // Convert the JSONObject to a RequestBody
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));


        Call<ResponseBody> call = apiService.login(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("RetrofitService", "Login Successful");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Failed to login: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }


    public void getUserFavorites(String userId, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.getUserFavorites(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("RetrofitService", "Get User Favorites Successful");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Failed to get user favorites: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void getUserRecipes(String userId, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.getUserRecipes(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("RetrofitService", "Get User Recipes Successful");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Failed to get user recipes: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }


    public void deleteRecipe(String recipeId, retrofit2.Callback<String> retrofitCallback) {
        Call<String> call = apiService.deleteRecipe(recipeId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
//                    Log.i("RetrofitService", "Recipe Delete Successful");
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle error
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void getUserById(String userId, String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        if (userId != null) {
            Call<ResponseBody> call = apiService.getUserById(userId, "Bearer " + token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            retrofitCallback.onResponse(call, response);
                        } else {
                            Log.e("RetrofitService", "Response body is null");
                            retrofitCallback.onFailure(call, new Throwable("Response body is null"));
                        }
                    } else {
                        Log.e("RetrofitService", "Error in response: " + response.message());
                        retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("RetrofitService", "Error calling API", t);
                    retrofitCallback.onFailure(call, t);
                }
            });
        } else {
            Log.e("NO_USER_ID", "No user id provided");
        }
    }

    public void markRecipeAsFavorite(String recipeId, String userId, retrofit2.Callback<ResponseBody> retrofitCallback) throws JSONException {
             Call<ResponseBody> call = apiService.markRecipeAsFavorite(recipeId, userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Error: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }


    public void updateUser(User user, String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        // Prepare text parameters
        RequestBody userIdBody = RequestBody.create(user.getId(), MediaType.parse("text/plain"));
        RequestBody usernameBody = RequestBody.create(user.getUsername(), MediaType.parse("text/plain"));
        RequestBody fullnameBody = RequestBody.create(user.getFullName(), MediaType.parse("text/plain"));
        RequestBody emailBody = RequestBody.create(user.getEmail(), MediaType.parse("text/plain"));
        RequestBody bioBody = RequestBody.create(user.getBio(), MediaType.parse("text/plain"));
        RequestBody roleBody = RequestBody.create(user.getRole(), MediaType.parse("text/plain"));

        // Prepare file parameter for user image (Handle null or empty path)
        MultipartBody.Part userImagePart = null;
        String userImagePath = user.getProfilePic();
        if (userImagePath != null && !userImagePath.isEmpty()) {
            File userImageFile = new File(userImagePath);
            if (userImageFile.exists()) {
                RequestBody fileBody = RequestBody.create(userImageFile, MediaType.parse("image/*"));
                userImagePart = MultipartBody.Part.createFormData("userImage", userImageFile.getName(), fileBody);
            }
        }

        // Make API call
        Call<ResponseBody> call = apiService.updateUser(userIdBody, usernameBody, fullnameBody, emailBody, bioBody, userImagePart, roleBody, "Bearer " + token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Error: " + t.getMessage());
                retrofitCallback.onFailure(call, t);
            }
        });
    }


    public void getUsers(String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.getUsers("Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        retrofitCallback.onResponse(call, response);
                    } else {
                        Log.e("UserService", "Response body is null");
                        retrofitCallback.onFailure(call, new Throwable("Response body is null"));
                    }
                } else {
                    Log.e("UserService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UserService", "Error calling API", t);
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void deleteUser(String recipeId, String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.deleteUser(recipeId, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle error
                Log.e("RetrofitService", "Error in response: " + t);
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void submitReview(String token, Review review, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.submitReview("Bearer " + token, review);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Error in response: " + t);
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void getReviews(String recipeId, String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.getReviews(recipeId, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Error in response: " + t);
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void getCategories(String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.getCategories("Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Error in response: " + t);
                retrofitCallback.onFailure(call, t);
            }
        });
    }

    public void createCategory(Category category, String token, retrofit2.Callback<ResponseBody> retrofitCallback) {
        Call<ResponseBody> call = apiService.createCategory(category, "Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    retrofitCallback.onResponse(call, response);
                } else {
                    Log.e("RetrofitService", "Error in response: " + response.message());
                    retrofitCallback.onFailure(call, new Throwable("Response Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RetrofitService", "Error in response: " + t);
                retrofitCallback.onFailure(call, t);
            }
        });
    }


}
