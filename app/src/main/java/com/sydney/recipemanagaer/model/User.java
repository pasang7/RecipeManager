package com.sydney.recipemanagaer.model;

public class User {
    private String fullName;
    private String email;
    private String username;
    private String bio;
    private String password;
    private String confirmPassword;
    private String profilePic;
    private String role;
    private String id;

    // for registering
    public User(String fullName, String email, String password, String confirmPassword) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // this one used for user response
    public User(String id, String fullName, String email, String username, String bio, String profilePic, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.bio = bio;
        this.profilePic = profilePic;
        this.username = username;
        this.role = role;
    }

    public User(String id, String fullName, String email, String username, String bio, String profilePic, String role, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.bio = bio;
        this.profilePic = profilePic;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
}
