package com.sydney.recipemanagaer.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sydney.recipemanagaer.model.repository.ReviewRepository;
import com.sydney.recipemanagaer.ui.viewmodel.ReviewViewModel;

public class ReviewViewModelFactory implements ViewModelProvider.Factory {
    private final ReviewRepository reviewRepository;

    public ReviewViewModelFactory(ReviewRepository repository) {
        this.reviewRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReviewViewModel.class)) {
            return (T) new ReviewViewModel(reviewRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
