package com.example.chatflow.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String messageId;
    private String message;
    private String senderId;
    private String senderUsername;
    private String senderProfilePicBase64;
    private Timestamp timestamp;
    private boolean isDeleted;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String messageId, String message, String senderId, String senderUsername, String senderProfilePicBase64, Timestamp timestamp, boolean isDeleted) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderProfilePicBase64 = senderProfilePicBase64;
        this.timestamp = timestamp;
        this.isDeleted = isDeleted;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderProfilePicBase64() {
        return senderProfilePicBase64;
    }

    public void setSenderProfilePicBase64(String senderProfilePicBase64) {
        this.senderProfilePicBase64 = senderProfilePicBase64;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}