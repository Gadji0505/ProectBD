package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.model.Report;
import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.repository.ArticleRepository;
import com.codewiki.codewiki.repository.ReportRepository;
import com.codewiki.codewiki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public AdminService(ArticleRepository articleRepository, 
                      UserRepository userRepository,
                      ReportRepository reportRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new NoSuchElementException("Article with ID " + articleId + " not found.");
        }
        articleRepository.deleteById(articleId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByReportDateDesc();
    }

    @Transactional
    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("Report with ID " + reportId + " not found."));
        report.setStatus(Report.Status.RESOLVED);
        reportRepository.save(report);
    }

    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("Report with ID " + reportId + " not found."));
        report.setStatus(Report.Status.REJECTED);
        reportRepository.save(report);
    }

    public List<Report> getReportsByStatus(Report.Status status) {
        return reportRepository.findByStatus(status);
    }
}