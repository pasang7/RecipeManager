package com.sydney.recipemanagaer.ui.viewmodel.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.viewmodel.UserViewModel;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private UserRepository repository;

    public UserViewModelFactory(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}