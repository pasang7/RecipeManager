package com.sydney.recipemanagaer.model.repository;

import static android.content.Context.MODE_PRIVATE;
import static java.util.ResourceBundle.clearCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.networking.retrofit.RetrofitClient;
import com.sydney.recipemanagaer.networking.retrofit.RetrofitService;
import com.sydney.recipemanagaer.utils.Util;

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

public class UserRepository {
    private MutableLiveData<String> loginStatus = new MutableLiveData<>();
    private Context context;
    private final RetrofitService retrofitService;
    private SharedPreferences sharedPreferences;
    private String token;


    public UserRepository(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Util.SHARED_PREFS_FILE, MODE_PRIVATE);
        retrofitService = new RetrofitService(context);
        this.token = getToken();
    }

    public MutableLiveData<List<User>> getUsers() {
        // This should be replaced with actual data fetching logic
        MutableLiveData<List<User>> liveData = new MutableLiveData<>();
        List<User> users = new ArrayList<>();

        retrofitService.getUsers(token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("data") && jsonObject.getJSONObject("data").has("users")) {
                            JSONArray userArray = jsonObject.getJSONObject("data").getJSONArray("users");
                            List<User> users = parseJsonToUsers(userArray);
                            liveData.postValue(users);
                        } else {
                            Log.e("API", "No user data found in response" + jsonObject);
                            liveData.postValue(null);
                        }
                    } catch (JSONException e) {
                        Log.e("API", "Error getting user data from JSON", e);
                        liveData.postValue(null);
                    } catch (IOException e) {
                        Log.e("API", "Error reading response body", e);
                        liveData.postValue(null);
                    }
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "Network error while fetching user", t);
                liveData.postValue(null);
            }
        });

        // Add more users
        liveData.setValue(users);
        return liveData;
    }

    public MutableLiveData<User> getUser(String userId) {
        MutableLiveData<User> liveData = new MutableLiveData<>();

        retrofitService.getUserById(userId, token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("data") && jsonObject.getJSONObject("data").has("user")) {
                            JSONObject userObject = jsonObject.getJSONObject("data").getJSONObject("user");
                            User user = parseJsonToUser(userObject);
                            liveData.postValue(user);
                        } else {
                            Log.e("API", "No user data found in response" + jsonObject);
                            liveData.postValue(null);
                        }
                    } catch (JSONException e) {
                        Log.e("API", "Error getting user data from JSON", e);
                        liveData.postValue(null);
                    } catch (IOException e) {
                        Log.e("API", "Error reading response body", e);
                        liveData.postValue(null);
                    }
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "Network error while fetching user", t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }


    private List<User> parseJsonToUsers(JSONArray jsonArray) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                User user = parseJsonToUser(jsonObject);
                users.add(user);
            } catch (JSONException e) {
                Log.e("API", "Error parsing user JSON", e);
            }
        }
        return users;
    }
    private User parseJsonToUser(JSONObject jsonObject) {
        try {
            String id = jsonObject.getString("_id");
            String email = jsonObject.getString("email");
            String username = jsonObject.optString("username", "");
            String fullName = jsonObject.optString("name", "");
            String profilePic = jsonObject.getString("userImage");
            String bio = jsonObject.optString("bio", "");
            String role = jsonObject.getString("role");

            return new User(id, fullName, email, username, bio, profilePic, role);
        } catch (JSONException e) {
            Log.e("API", "Error parsing user JSON", e);
            return null;  // Return null or throw, depending on how you want to handle parsing errors
        }
    }



    public LiveData<String> updateUser(User user) {
        // update logic
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        MutableLiveData<String> result = new MutableLiveData<>();
        retrofitService.updateUser(user, token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue("Update Successful");
                } else {
                    result.setValue("Error updating user: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue("Error updating user: " + t.getMessage());
            }
        });

        return result;
    }

    public LiveData<String> deleteUser(String id) {
        MutableLiveData<String> responseData = new MutableLiveData<>();

        retrofitService.deleteUser(id, token, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success
                    responseData.setValue("Deleted successfully");
                } else {
                    // Handle response error
                    responseData.setValue("Error deleting user: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                responseData.setValue("Error deleting user: " + t.getMessage());
            }
        });

        return responseData;
    }

    public LiveData<String> signup(User user) throws JSONException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        MutableLiveData<String> result = new MutableLiveData<>();
        retrofitService.register(user, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue("Signup Successful");
                } else {
                    result.setValue("Error creating user: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue("Error creating user: " + t.getMessage());
            }
        });
        return result;
    }

    public LiveData<String> getLoginStatus() {
        return loginStatus;
    }

    public LiveData<String> login(String email, String password) throws JSONException {
        retrofitService.login(email, password, new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String token = jsonObject.getString("token");
                        JSONObject userData = jsonObject.getJSONObject("data").getJSONObject("user");
                        String userId = userData.getString("_id");  // or "_id" depending on your backend

                        // Store the token securely, then update LiveData
                        saveToken(token, userId); // Implement this method to save token securely
                        loginStatus.postValue("Login successful");
                    } catch (JSONException | IOException e) {
                        loginStatus.postValue("Failed to parse response: " + e.getMessage());
                    }
                } else {
                    loginStatus.postValue("Response Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginStatus.postValue("Failed to login: " + t.getMessage());
            }
        });

        return loginStatus;
    }

    private void saveToken(String token, String userId) {
        // Use SharedPreferences or secure storage to save the token
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Util.TOKEN_KEY, token);
        editor.putString(Util.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(Util.TOKEN_KEY, null);
    }

    public String getLoggedInUserId() {
        return sharedPreferences.getString(Util.USER_ID_KEY, null);
    }

    public LiveData<String> updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        MutableLiveData<String> result = new MutableLiveData<>();
        retrofitService.updatePassword(token, currentPassword, newPassword, confirmPassword, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.postValue("Password Updated Successfully");
                } else {
                    result.postValue("Error Updating Password: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.postValue("Error: " + t.getMessage());
            }
        });
        return result;
    }

    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        // Update Retrofit client
        RetrofitClient.clearRetrofitInstance(); // Clear the existing client

        // Additional steps to ensure a complete cleanup
        clearCache();
    }
}
