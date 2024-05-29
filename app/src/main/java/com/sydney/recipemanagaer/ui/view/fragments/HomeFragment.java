package com.sydney.recipemanagaer.ui.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sydney.recipemanagaer.R;
import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.repository.CategoryRepository;
import com.sydney.recipemanagaer.model.repository.RecipeRepository;
import com.sydney.recipemanagaer.ui.view.adapters.GenericRecipeAdapter;
import com.sydney.recipemanagaer.ui.viewmodel.CategoryViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.RecipeViewModel;
import com.sydney.recipemanagaer.ui.viewmodel.factory.CategoryViewModelFactory;
import com.sydney.recipemanagaer.ui.viewmodel.factory.RecipeViewModelFactory;
import com.sydney.recipemanagaer.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements GenericRecipeAdapter.OnRecipeClickListener {
    private RecyclerView recyclerRecentRecipes;
    private RecyclerView recyclerPopularPosts;
    private RecipeViewModel viewModel;
    private CategoryViewModel categoryViewModel;
    private RecyclerView recyclerSearchResults;
    private List<Recipe> allRecipes = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private GenericRecipeAdapter searchAdapter;
    private LinearLayout linearLayoutCategoryButtons;
    TextView searchResultLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecipeRepository recipeRepository = new RecipeRepository(getContext());
        viewModel = new ViewModelProvider(this, new RecipeViewModelFactory(recipeRepository)).get(RecipeViewModel.class);

        CategoryRepository categoryRepository = new CategoryRepository(getContext());
        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(categoryRepository)).get(CategoryViewModel.class);

        recyclerRecentRecipes = view.findViewById(R.id.recycler_recent_recipes);
        recyclerPopularPosts = view.findViewById(R.id.recycler_popular_posts);
        linearLayoutCategoryButtons = view.findViewById(R.id.linearLayoutCategoryButtons);

        // RecyclerView for search results
        searchResultLabel = view.findViewById(R.id.search_result_label);
        recyclerSearchResults = view.findViewById(R.id.search_results_list);
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        searchAdapter = new GenericRecipeAdapter(allRecipes, this);
        recyclerSearchResults.setAdapter(searchAdapter);

        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            allRecipes = recipes;
            setupRecyclerView(recyclerRecentRecipes, recipes);
            setupRecyclerView(recyclerPopularPosts, recipes);
        });

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories = categories;
            Log.d("category", categories.toString());
            createCategoryButtons();
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleSearch(newText);
                return true;
            }
        });

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<Recipe> recipes) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new GenericRecipeAdapter(recipes, this));
    }

    private void createCategoryButtons() {
        linearLayoutCategoryButtons.removeAllViews();

        for (Category category : categories) {
            Button button = new Button(getContext(), null, 0, R.style.CategoryButtonStyle);
            button.setText(category.getName());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = (int) getResources().getDimension(R.dimen.button_margin);
            params.setMargins(margin, margin, margin, margin);
            button.setLayoutParams(params);
            button.setOnClickListener(v -> handleCategoryFilter(category));
            linearLayoutCategoryButtons.addView(button);
        }
    }


    @Override
    public void onRecipeClick(Recipe recipe) {
        Util.handleViewRecipeDetail(recipe, getActivity());
    }

    private List<Recipe> filterRecipes(String query) {
        if (allRecipes == null || query.isEmpty()) {
            // Handle the case where allRecipes is null, e.g., show a message to the user
            Log.e("FilterRecipe", "No recipes");
            return new ArrayList<>();
        }

        String lowerCaseQuery = query.toLowerCase();

        return allRecipes.stream()
                .filter(recipe -> recipe.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        recipe.getIngredients().stream().anyMatch(ingredient ->
                                ingredient.toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    public void handleSearch(String value) {
        if (value.isEmpty()) {
            searchAdapter.updateRecipes(new ArrayList<>());
            searchResultLabel.setVisibility(View.GONE);
        } else {
            searchAdapter.updateRecipes(filterRecipes(value));
            searchResultLabel.setVisibility(View.VISIBLE);
        }
    }

    private void handleCategoryFilter(Category category) {
        List<Recipe> filteredRecipes = allRecipes.stream()
                .filter(recipe -> {
                    String categoryJsonString = recipe.getCategoryId();
                    if (categoryJsonString != null) {
                        try {
                            JSONObject categoryJson = new JSONObject(categoryJsonString);
                            String categoryName = categoryJson.getString("name");
                            return categoryName.equals(category.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());

        searchAdapter.updateRecipes(filteredRecipes);
        searchResultLabel.setVisibility(View.VISIBLE);
    }

}
