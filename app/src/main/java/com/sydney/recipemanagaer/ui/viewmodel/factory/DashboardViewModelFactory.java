package com.sydney.recipemanagaer.ui.viewmodel.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.model.repository.UserRepository;
import com.sydney.recipemanagaer.ui.viewmodel.DashboardViewModel;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {
    private UserRepository repository;

    public DashboardViewModelFactory(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}