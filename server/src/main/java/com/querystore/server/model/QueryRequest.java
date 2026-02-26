package com.querystore.server.model;

import lombok.Data;

public class QueryRequest {
    private String title;
    private String content;
    private String username; // To identify which user this belongs to

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
