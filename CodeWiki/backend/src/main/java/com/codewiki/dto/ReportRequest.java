package com.codewiki.dto;

import com.codewiki.models.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportRequest {
    private ReportTargetType targetType;  // ARTICLE, COMMENT, USER
    private Long targetId;               // ID цели
    @NotBlank(message = "Reason is required")
    private String reason;
}