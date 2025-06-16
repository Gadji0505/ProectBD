package com.codewiki.dao;

import com.codewiki.model.Report;
import com.codewiki.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDao {

    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY reported_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(mapReportFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public Report getReportById(int id) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapReportFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createReport(Report report) {
        String sql = "INSERT INTO reports (article_id, comment_id, reporter_id, reason) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, report.getArticleId() > 0 ? report.getArticleId() : null);
            pstmt.setObject(2, report.getCommentId() > 0 ? report.getCommentId() : null);
            pstmt.setInt(3, report.getReporterId());
            pstmt.setString(4, report.getReason());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resolveReport(int id) {
        String sql = "UPDATE reports SET status = 'resolved' WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectReport(int id) {
        String sql = "UPDATE reports SET status = 'rejected' WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReport(int id) {
        String sql = "DELETE FROM reports WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Report mapReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("id"));
        report.setArticleId(rs.getInt("article_id"));
        report.setCommentId(rs.getInt("comment_id"));
        report.setReporterId(rs.getInt("reporter_id"));
        report.setReason(rs.getString("reason"));
        report.setReportedAt(rs.getTimestamp("reported_at"));
        report.setStatus(rs.getString("status"));
        return report;
    }
}