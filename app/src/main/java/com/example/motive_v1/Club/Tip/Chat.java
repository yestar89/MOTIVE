package com.example.motive_v1.Club.Tip;

public class Chat {

    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    String message, sendBy;

    public Chat(String message, String sendBy) {
        this.message = message;
        this.sendBy = sendBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }
}
