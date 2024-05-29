package com.sydney.recipemanagaer.ui.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Review;
import com.sydney.recipemanagaer.model.repository.RecipeRepository;
import com.sydney.recipemanagaer.model.repository.ReviewRepository;
import com.sydney.recipemanagaer.ui.view.adapters.ImageAdapter;
import com.sydney.recipemanagaer.ui.viewmodel.RecipeViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.ReviewViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.RecipeViewModelFactory;
import com.sydney.recipemanagaer.ui.viewmodel.factory.ReviewViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private TextView textViewTitle, textViewFoodType, textViewDescription, textViewIngredients, textViewInstructions, textViewCookingTime;
    private ImageView imageViewRecipe;
    private ImageButton buttonEditRecipe, buttonDeleteRecipe, favoriteButton, buttonShareRecipe, buttonReviewRecipe;
    private Button submitReviewButton;
    private RecipeViewModel viewModel;
    private RecyclerView imageRecyclerView;
    private ArrayList<Object> images = new ArrayList<>();
    private ImageAdapter imageAdapter;

    private RatingBar ratingBar;
    private EditText reviewEditText;
    ReviewViewModel reviewViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        RecipeRepository recipeRepository = new RecipeRepository(getContext());
        viewModel = new ViewModelProvider(this, new RecipeViewModelFactory(recipeRepository)).get(RecipeViewModel.class);

        ReviewRepository reviewRepository = new ReviewRepository(getContext());
        reviewViewModel = new ViewModelProvider(this, new ReviewViewModelFactory(reviewRepository)).get(ReviewViewModel.class);


        // Initialize UI components
        initializeUI(view);

        // Retrieve and display recipe details
        displayRecipeDetails();

        // Setup button listeners
        setupButtonListeners();

        return view;
    }

    private void initializeUI(View view) {
        textViewTitle = view.findViewById(R.id.textViewRecipeTitle);
        textViewDescription = view.findViewById(R.id.textViewRecipeDescription);
        textViewIngredients = view.findViewById(R.id.textViewRecipeIngredients);
        textViewInstructions = view.findViewById(R.id.textViewRecipeInstructions);
        textViewCookingTime = view.findViewById(R.id.textViewRecipeCookingTime);
        imageViewRecipe = view.findViewById(R.id.imageViewRecipe);
        buttonEditRecipe = view.findViewById(R.id.buttonUpdateRecipe); // Button for editing
        buttonDeleteRecipe = view.findViewById(R.id.buttonDeleteRecipe); // Initialize the Delete Button
        favoriteButton = view.findViewById(R.id.buttonMarkFavorite);  // Initialize the fav button
        buttonReviewRecipe = view.findViewById(R.id.buttonReviewRecipe); // Initialize review button
        buttonShareRecipe = view.findViewById(R.id.buttonShareRecipe); // Initialize the share button
        textViewFoodType = view.findViewById(R.id.textViewFoodType);
        imageRecyclerView = view.findViewById(R.id.recipeImagesRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(images); // Assuming 'images' is the list of images
        imageRecyclerView.setAdapter(imageAdapter);

        // review
        ratingBar = view.findViewById(R.id.ratingBar);
        reviewEditText = view.findViewById(R.id.reviewEditText);
        submitReviewButton = view.findViewById(R.id.submitReviewButton);

    }

    private void displayRecipeDetails() {
        Bundle args = getArguments();
        if (args != null) {
            textViewTitle.setText(args.getString("title"));
            textViewDescription.setText(args.getString("description"));
            textViewIngredients.setText(args.getString("ingredients"));
            textViewInstructions.setText(args.getString("instructions"));
            textViewCookingTime.setText("Time: " + args.getString("cookingTime") + " minutes");

            // Extract and set the category name
            String categoryJsonString = args.getString("category");
            if (categoryJsonString != null) {
                try {
                    JSONObject categoryJson = new JSONObject(categoryJsonString);
                    String categoryName = categoryJson.getString("name");
                    textViewFoodType.setText(categoryName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Load the featured image using Glide
            String imageUrl = args.getString("imageUrl");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(Util.getBaseURL() + "recipe/images/" + imageUrl)
                        .placeholder(R.drawable.placeholder_image_background)
                        .error(R.drawable.error_image)
                        .into(imageViewRecipe);
            }

            // Load existing images
            List<String> imageURLs = args.getStringArrayList("imagesUrl");
            if (imageURLs != null && !imageURLs.isEmpty()) {
                for (String url : imageURLs) {
                    images.add(url);
                }
                imageAdapter.notifyDataSetChanged();
            }

        }
    }

    private void setupButtonListeners() {
        buttonEditRecipe.setOnClickListener(v -> navigateToUpdateFragment());
        buttonDeleteRecipe.setOnClickListener(v -> deleteRecipe());

        favoriteButton.setOnClickListener(v -> {
            try {
                markRecipeAsFavorite();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        // Review button listener
        buttonReviewRecipe.setOnClickListener(v -> navigateToReviewFragment());

        // Share button listener
        buttonShareRecipe.setOnClickListener(v -> shareRecipeDetails());

        submitReviewButton.setOnClickListener(v -> submitReview());

    }

    private void navigateToUpdateFragment() {
        Bundle args = getArguments(); // Use existing recipe details
        UpdateRecipeFragment fragment = new UpdateRecipeFragment();
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteRecipe() {
        String recipeId = getArguments().getString("recipeId");
        viewModel.deleteRecipe(recipeId).observe(getViewLifecycleOwner(), result -> {
            if ("Deleted successfully".equals(result)) {
                Toast.makeText(getContext(), "Recipe deleted.", Toast.LENGTH_SHORT).show();
                navigateToHome();
            } else {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        if (isAdded() && getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
            bottomNavigationView.setSelectedItemId(R.id.homeFragment);
        }
    }

    public void markRecipeAsFavorite() throws JSONException {
        {
            String recipeId = getArguments().getString("recipeId");
            viewModel.markAsFavorite(recipeId).observe(getViewLifecycleOwner(), result -> {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String reviewText = reviewEditText.getText().toString();
        String recipeId = getArguments().getString("recipeId");

        if (rating == 0) {
            Toast.makeText(getContext(), "Please provide a rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review(reviewText, rating, recipeId);

        reviewViewModel.submitReview(review).observe(getViewLifecycleOwner(), result -> {
            if ("Review submitted successfully.".equals(result)) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                // Optionally clear the review form
                ratingBar.setRating(0);
                reviewEditText.setText("");
            } else {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void navigateToReviewFragment() {
        Bundle args = getArguments();
        RecipeReviewFragment fragment = new RecipeReviewFragment();
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void shareRecipeDetails() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        StringBuilder recipeDetails = new StringBuilder();
        recipeDetails.append("Recipe Title: ").append(textViewTitle.getText().toString()).append("\n\n");
        recipeDetails.append("Description: ").append(textViewDescription.getText().toString()).append("\n\n");
        recipeDetails.append("Ingredients: ").append(textViewIngredients.getText().toString()).append("\n\n");
        recipeDetails.append("Instructions: ").append(textViewInstructions.getText().toString()).append("\n\n");
        recipeDetails.append("Cooking Time: ").append(textViewCookingTime.getText().toString()).append("\n\n");

        // Add the recipe details to the intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails.toString());
        startActivity(Intent.createChooser(shareIntent, "Share Recipe"));
    }

}
