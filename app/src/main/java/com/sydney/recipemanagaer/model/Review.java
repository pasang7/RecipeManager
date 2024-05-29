package com.sydney.recipemanagaer.model;

public class Review {
    private String id;
    private String recipeId;
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
    public Review(String review, float rating, String recipeId) {
        this.rating = rating;
        this.review = review;
        this.recipeId = recipeId;
    }

    public Review(String review, float rating, String recipeId, String reviewerName) {
        this.rating = rating;
        this.review = review;
        this.recipeId = recipeId;
        this.reviewerName = reviewerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
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

