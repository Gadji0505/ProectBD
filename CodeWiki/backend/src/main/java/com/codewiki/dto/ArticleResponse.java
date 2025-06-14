package com.codewiki.dto;

import com.codewiki.models.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ArticleResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String authorName;
    private ArticleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> tags;
    private int upvotes;
    private int downvotes;
}