package com.sydney.recipemanagaer.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sydney.recipemanagaer.model.Recipe;
import com.sydney.recipemanagaer.model.repository.RecipeRepository;

import org.json.JSONException;

import java.util.List;

public class RecipeViewModel extends ViewModel {
    private RecipeRepository recipeRepository;

    public RecipeViewModel(RecipeRepository repository) {
        this.recipeRepository = repository;
    }
    public LiveData<String> createRecipe(Recipe recipe) {
        return recipeRepository.createRecipe(recipe);
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipeRepository.getRecipes();
    }

    public LiveData<String> deleteRecipe(String recipeId) {
        return recipeRepository.deleteRecipe(recipeId);
    }

    public LiveData<String> updateRecipe(Recipe recipe) {
        return recipeRepository.updateRecipe(recipe);
    }

    public LiveData<String> markAsFavorite(String recipeId) throws JSONException {
       return recipeRepository.markAsFavorite(recipeId);
    }

    public LiveData<List<Recipe>> getUserFavorites() {
        return recipeRepository.getUserFavorites();
    }

    public LiveData<List<Recipe>> getUserRecipes() {
        return recipeRepository.getUserRecipes();
    }

}
