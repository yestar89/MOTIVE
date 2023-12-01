package com.example.motive_v1.Club.Feed;

public class Post {
    public Post() {
    }
    private String postid;
    private String username;
    private String userimage;

    private String postimage;
    private String description;
    private String category;

    //추가
    private String key;
    //추가
    private String publisher;




    public Post(String username, String userimage, String postid, String postimage, String description, String category, String publisher) {
        this.postid = postid;
        this.username = username;
        this.postimage = postimage;
        this.description = description;
        this.category = category;
        this.userimage = userimage;
        this.publisher = publisher;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }
    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public Post(long timestamp) {
        this.timestamp = timestamp;
    }
}
