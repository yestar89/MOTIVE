package com.example.motive_v1.User;

public class UserProfile {
    private String username;
    private String gender;
    private String profileImageUrl;
    private String description;

    public UserProfile() {

    }



    public UserProfile(String username, String gender, String description, String profileImageUrl) {
        this.username = username;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
