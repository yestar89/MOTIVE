package com.example.motive_v1.Club.Feed;

public class Comment {
    //식별 속성
    private String commentid;
    private String comment;
    private String publisher;

    public Comment(String comment, String publisher, String commentid) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
    }
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Comment() {
    }
}
