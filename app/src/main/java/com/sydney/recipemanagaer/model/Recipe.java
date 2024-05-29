package com.sydney.recipemanagaer.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private final String title;
    private final String description;
    private final List<String> ingredients;
    private final String instructions;
    private final int cookingTime;
    private String featuredImgURL;
    private String recipeId;

    private List<String> favoritedBy;
    private String featuredImage;

    private ArrayList<String> images;
    private String categoryId;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    private String categoryName;

    public Recipe(String title, String description, List<String> ingredients, String instructions, int cookingTime) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
    }

    public Recipe(String recipeId, String title, String description, List<String> ingredients, String instructions, int cookingTime) {
        this.recipeId = recipeId;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // Getter for the name
    public String getTitle() {
        return title;
    }

    // Getter for the description
    public String getDescription() {
        return description;
    }

    // Getter for ingredients
    public List<String> getIngredients() {
        return ingredients;
    }

    // Getter for instructions
    public String getInstructions() {
        return instructions;
    }

    // Getter for cooking time
    public int getCookingTime() {
        return cookingTime;
    }

    // Getter for the featured image URL
    public String getFeaturedImgURL() {
        return featuredImgURL;
    }

    public void setFeaturedImgURL(String featuredImgURL) {
        this.featuredImgURL = featuredImgURL;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public List<String> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(List<String> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }

    public String getFeaturedImage() {
        return this.featuredImage;
    }

    public void setFeaturedImage(String imageUri) {
        this.featuredImage = imageUri;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
