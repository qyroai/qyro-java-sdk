package com.qyroai.sdk.models;

public class Message {
    private String id;
    private String role;
    private String content;

    public Message() {
    }

    public Message(String id, String role, String content) {
        this.id = id;
        this.role = role;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}