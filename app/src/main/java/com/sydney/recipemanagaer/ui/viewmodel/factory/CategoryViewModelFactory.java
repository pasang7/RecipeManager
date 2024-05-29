package com.sydney.recipemanagaer.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.model.repository.CategoryRepository;
import com.sydney.recipemanagaer.ui.viewmodel.CategoryViewModel;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {
    private final CategoryRepository categoryRepository;

    public CategoryViewModelFactory(CategoryRepository repository) {
        this.categoryRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(categoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
