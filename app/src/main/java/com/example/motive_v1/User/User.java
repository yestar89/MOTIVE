package com.example.motive_v1.User;

public class User {

    private String email;
    private String pw;
    private String userId;
    private String username;
    private String profileImageUrl;
    private String gender;
    private String description;

    // 갓생러 위함
    private String goalText; // 이 사용자의 주요 목표
    private int totalTime; // 해당 목표에 대한 총 시간

    public String getGoalText() {
        return goalText;
    }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }


    public User(String userId) {
        this.userId = userId;
    }

    public User(String email, String username, String profileImageUrl, String pw, String gender, String description) {
        this.email = email;
        this.pw = pw;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.description = description;
    }

    public User(String username, String gender, String description, String profileImageUrl) {
        this.username = username;
        this.gender = gender;
        this.description = description;   // 여기 부분!!
        this.profileImageUrl = profileImageUrl;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

