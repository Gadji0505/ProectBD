package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Report;
import com.codewiki.codewiki.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByReportDateDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByStatus(Report.Status status) {
        return reportRepository.findByStatusOrderByReportDateDesc(status);
    }

    @Transactional
    public Report saveReport(Report report) {
        if (report.getDescription() == null || report.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Report description cannot be empty");
        }
        
        if (report.getStatus() == null) {
            report.setStatus(Report.Status.OPEN);
        }
        
        if (report.getReportDate() == null) {
            report.setReportDate(java.time.LocalDateTime.now());
        }
        
        return reportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    @Transactional
    public Optional<Report> resolveReport(Long reportId) {
        return reportRepository.findById(reportId).map(report -> {
            report.setStatus(Report.Status.RESOLVED);
            return reportRepository.save(report);
        });
    }

    @Transactional
    public Optional<Report> rejectReport(Long reportId) {
        return reportRepository.findById(reportId).map(report -> {
            report.setStatus(Report.Status.REJECTED);
            return reportRepository.save(report);
        });
    }

    @Transactional(readOnly = true)
    public List<Report> getLatestReports(int limit) {
        return reportRepository.findAllByOrderByReportDateDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    // Новый метод для получения описания отчета
    public String getReportDescription(Report report) {
        return report.getDescription();
    }
}