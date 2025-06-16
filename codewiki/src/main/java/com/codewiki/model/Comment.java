package com.codewiki.model;

import java.util.Date;

public class Comment {
    private int id;
    private int articleId;
    private int authorId;
    private String content;
    private Date createdAt;

    // Getters
    public int getId() {
        return id;
    }

    public int getArticleId() {
        return articleId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}