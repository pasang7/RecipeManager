package com.sydney.recipemanagaer.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sydney.recipemanagaer.model.Review;
import com.sydney.recipemanagaer.model.repository.ReviewRepository;

import java.util.List;

public class ReviewViewModel extends ViewModel {
    private ReviewRepository reviewRepository;

    public ReviewViewModel(ReviewRepository repository) {
        this.reviewRepository = repository;
    }

    public LiveData<String> submitReview(Review review) {
        return reviewRepository.submitReview(review);
    }

    public LiveData<List<Review>> getReviews(String recipeId) {
        return reviewRepository.getReviews(recipeId);
    }

}
