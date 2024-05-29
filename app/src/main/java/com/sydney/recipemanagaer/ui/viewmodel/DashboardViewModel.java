package com.sydney.recipemanagaer.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.sydney.recipemanagaer.model.User;
import com.sydney.recipemanagaer.model.repository.UserRepository;

public class DashboardViewModel extends ViewModel {
    private UserRepository userRepository;

    public DashboardViewModel() {
        loadUser();
    }

    public DashboardViewModel(UserRepository repository) {
        this.userRepository = repository;
    }

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdminLiveData = new MutableLiveData<>();



    private void loadUser() {
        User user = userRepository.getUser(userRepository.getLoggedInUserId()).getValue();  // Get the user from the repository
        userLiveData.setValue(user);
        isAdminLiveData.setValue(user.getRole() == "admin" ? true : false); // Assume User model has an isAdmin method
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getIsAdminLiveData() {
        return isAdminLiveData;
    }


}
