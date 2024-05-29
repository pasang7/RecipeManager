package com.sydney.recipemanagaer.ui.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sydney.recipemanagaer.model.Category;
import com.sydney.recipemanagaer.model.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private  CategoryRepository categoryRepository;

    public CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public LiveData<String> addCategory(Category category) {
        return categoryRepository.addCategory(category);
    }

    public LiveData<List<Category>> getCategories() {
        return categoryRepository.getCategories();
    }
}

