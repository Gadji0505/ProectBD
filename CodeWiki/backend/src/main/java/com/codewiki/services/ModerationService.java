package com.codewiki.services;

import com.codewiki.models.Report;
import com.codewiki.models.ReportStatus;
import com.codewiki.repositories.ReportRepository;
import com.codewiki.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ReportRepository reportRepo;
    private final UserRepository userRepo;

    public Report createReport(Long targetId, String targetType, String reason, Long reportingUserId) {
        User reportingUser = userRepo.findById(reportingUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = new Report();
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setReportingUser(reportingUser);
        return reportRepo.save(report);
    }

    public Report resolveReport(Long reportId) {
        Report report = reportRepo.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(ReportStatus.CLOSED);
        return reportRepo.save(report);
    }
}