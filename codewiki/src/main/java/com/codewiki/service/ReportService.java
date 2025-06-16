package com.codewiki.service;

import com.codewiki.dao.ReportDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.CommentDao;
import com.codewiki.dao.UserDao;
import com.codewiki.model.Report;
import java.util.List;

public class ReportService {
    private final ReportDao reportDao = new ReportDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final CommentDao commentDao = new CommentDao();
    private final UserDao userDao = new UserDao();

    public List<Report> getAllReports() {
        List<Report> reports = reportDao.getAllReports();
        for (Report report : reports) {
            if (report.getArticleId() > 0) {
                report.setReportedArticle(articleDao.getArticleById(report.getArticleId()));
            }
            if (report.getCommentId() > 0) {
                report.setReportedComment(commentDao.getCommentById(report.getCommentId()));
            }
            report.setReporter(userDao.getUserById(report.getReporterId()));
        }
        return reports;
    }

    public Report getReportById(int id) {
        Report report = reportDao.getReportById(id);
        if (report != null) {
            if (report.getArticleId() > 0) {
                report.setReportedArticle(articleDao.getArticleById(report.getArticleId()));
            }
            if (report.getCommentId() > 0) {
                report.setReportedComment(commentDao.getCommentById(report.getCommentId()));
            }
            report.setReporter(userDao.getUserById(report.getReporterId()));
        }
        return report;
    }

    public boolean createReport(Report report) {
        return reportDao.createReport(report);
    }

    public boolean resolveReport(int id) {
        return reportDao.resolveReport(id);
    }

    public boolean rejectReport(int id) {
        return reportDao.rejectReport(id);
    }

    public boolean deleteReport(int id) {
        return reportDao.deleteReport(id);
    }

    public List<Report> getPendingReports() {
        List<Report> reports = reportDao.getReportsByStatus("pending");
        for (Report report : reports) {
            if (report.getArticleId() > 0) {
                report.setReportedArticle(articleDao.getArticleById(report.getArticleId()));
            }
            if (report.getCommentId() > 0) {
                report.setReportedComment(commentDao.getCommentById(report.getCommentId()));
            }
            report.setReporter(userDao.getUserById(report.getReporterId()));
        }
        return reports;
    }

    public int getPendingReportsCount() {
        return reportDao.getReportsByStatus("pending").size();
    }
}