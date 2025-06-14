package com.codewiki.dto;

import com.codewiki.models.ReportStatus;
import com.codewiki.models.ReportTargetType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private Long id;
    private ReportTargetType targetType;
    private Long targetId;
    private String reportingUserName;
    private String reason;
    private ReportStatus status;
    private LocalDateTime timestamp;
}