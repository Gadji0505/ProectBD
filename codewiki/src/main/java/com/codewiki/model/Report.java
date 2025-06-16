package com.codewiki.model;

import java.sql.Timestamp;

public class Report {
    private int id;
    private int articleId;
    private int commentId;
    private int reporterId;
    private String reason;
    private Timestamp reportedAt;
    private String status; // "pending", "resolved", "rejected"

    // Getters
    public int getId() {
        return id;
    }

    public int getArticleId() {
        return articleId;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getReportedAt() {
        return reportedAt;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setReportedAt(Timestamp reportedAt) {
        this.reportedAt = reportedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}