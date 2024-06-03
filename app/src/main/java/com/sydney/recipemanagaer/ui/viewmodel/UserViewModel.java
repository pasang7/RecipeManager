package com.sydney.recipemanagaer.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.model.repository.UserRepository;

import org.json.JSONException;

import java.util.List;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    public UserViewModel(UserRepository repository) {
        this.userRepository = repository;
    }

    public LiveData<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    public LiveData<String> login(String email, String password) throws JSONException {
       return userRepository.login(email, password);
    }

    public LiveData<String> signup(User user) throws JSONException {
        return userRepository.signup(user);
    }

    public LiveData<String>  deleteUser(String userId) {
        return userRepository.deleteUser(userId);
    }

    public LiveData<String> updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public String getToken() {
        return userRepository.getToken();
    }

    public String getLoggedInUserId() {
        return userRepository.getLoggedInUserId();
    }

    public LiveData<User> getUser() {
        return userRepository.getUser(getLoggedInUserId());
    }

    public LiveData<String> updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        return userRepository.updatePassword(currentPassword, newPassword, confirmPassword);
    }

    public void clearSession() {
        userRepository.clearSession();
    }

}
