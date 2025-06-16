package com.codewiki.model;

import java.sql.Timestamp;

public class Revision {
    private int id;
    private int articleId;
    private int authorId;
    private String changes;
    private Timestamp revisedAt;

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

    public String getChanges() {
        return changes;
    }

    public Timestamp getRevisedAt() {
        return revisedAt;
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

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public void setRevisedAt(Timestamp revisedAt) {
        this.revisedAt = revisedAt;
    }
}