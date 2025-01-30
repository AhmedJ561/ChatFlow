package com.example.chatflow.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String email;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String profilePicBase64;

    public UserModel() {
    }

    public UserModel(String email, String username, Timestamp createdTimestamp, String userId, String profilePicBase64) {
        this.email = email;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.profilePicBase64 = profilePicBase64;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicBase64() {
        return profilePicBase64;
    }

    public void setProfilePicBase64(String profilePicBase64) {
        this.profilePicBase64 = profilePicBase64;
    }
}
