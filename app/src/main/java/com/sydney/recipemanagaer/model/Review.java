package com.sydney.recipemanagaer.model;

public class Review {
    private String id;
    private String recipe;
    private String userId;
    private String review;
    private float rating;

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    private String reviewerName;


    // Constructor
    public Review(String review, float rating, String recipe) {
        this.rating = rating;
        this.review = review;
        this.recipe = recipe;
    }

    public Review(String review, float rating, String recipe, String reviewerName) {
        this.rating = rating;
        this.review = review;
        this.recipe = recipe;
        this.reviewerName = reviewerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
// Getter and Setter methods for username

}

