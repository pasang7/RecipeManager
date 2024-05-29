package com.sydney.recipemanagaer.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Review;
import com.sydney.recipemanagaer.model.repository.ReviewRepository;
import com.sydney.recipemanagaer.ui.view.adapters.ReviewAdapter;
import com.sydney.recipemanagaer.ui.viewmodel.ReviewViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.ReviewViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class RecipeReviewFragment extends Fragment {
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviews;
    private ReviewViewModel reviewViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_review, container, false);

        // Initialize RecyclerView and reviews list
        reviewsRecyclerView = rootView.findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize reviews list and adapter
        reviews = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviews);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        // Initialize ReviewRepository and ReviewViewModel
        ReviewRepository reviewRepository = new ReviewRepository(getContext());
        reviewViewModel = new ViewModelProvider(this, new ReviewViewModelFactory(reviewRepository)).get(ReviewViewModel.class);

        // Fetch and observe reviews
        fetchReviews();

        return rootView;
    }

    private void fetchReviews() {
        String recipeId = getArguments().getString("recipeId");
        reviewViewModel.getReviews(recipeId).observe(getViewLifecycleOwner(), reviews -> {
            if (reviews != null) {
                reviewAdapter.updateReviews(reviews);
            } else {
                Toast.makeText(getContext(), "Error fetching reviews", Toast.LENGTH_LONG).show();
            }
        });
    }
}
