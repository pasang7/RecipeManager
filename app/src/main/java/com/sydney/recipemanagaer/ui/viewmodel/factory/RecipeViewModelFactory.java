package com.sydney.recipemanagaer.ui.viewmodel.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.model.repository.RecipeRepository;
import com.sydney.recipemanagaer.ui.viewmodel.RecipeViewModel;

public class RecipeViewModelFactory implements ViewModelProvider.Factory {
    private RecipeRepository repository;

    public RecipeViewModelFactory(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeViewModel.class)) {
            return (T) new RecipeViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}