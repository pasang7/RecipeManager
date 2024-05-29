package com.sydney.recipemanagaer.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.repository.RecipeRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private RecipeRepository recipeRepository;
    public HomeViewModel(RecipeRepository repository) {
        this.recipeRepository = repository;
    }
    private LiveData<List<Recipe>> recipes;

    public HomeViewModel() {
        recipes = recipeRepository.getRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}
