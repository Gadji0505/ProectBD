package com.codewiki.model;

import java.util.Date;
import java.util.List;

public class Article {
    private int id;
    private String title;
    private String slug;
    private String content;
    private int authorId;
    private Date createdAt;
    private Date updatedAt;
    private String status;
    private List<Category> categories;
    private List<Tag> tags;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getContent() {
        return content;
    }

    public int getAuthorId() {
        return authorId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Tag> getTags() {
        return tags;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}