package com.codewiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Content is required")
    private String content;

    private Long articleId;  // ID статьи, к которой относится комментарий
}