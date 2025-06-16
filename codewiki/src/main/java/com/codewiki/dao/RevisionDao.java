package com.codewiki.dao;

import com.codewiki.model.Revision;
import com.codewiki.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevisionDao {

    public List<Revision> getRevisionsByArticle(int articleId) {
        List<Revision> revisions = new ArrayList<>();
        String sql = "SELECT * FROM revisions WHERE article_id = ? ORDER BY revised_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    revisions.add(mapRevisionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revisions;
    }

    public Revision getRevisionById(int id) {
        String sql = "SELECT * FROM revisions WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRevisionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createRevision(Revision revision) {
        String sql = "INSERT INTO revisions (article_id, author_id, changes) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, revision.getArticleId());
            pstmt.setInt(2, revision.getAuthorId());
            pstmt.setString(3, revision.getChanges());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRevision(int id) {
        String sql = "DELETE FROM revisions WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRevisionsForArticle(int articleId) {
        String sql = "DELETE FROM revisions WHERE article_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Revision mapRevisionFromResultSet(ResultSet rs) throws SQLException {
        Revision revision = new Revision();
        revision.setId(rs.getInt("id"));
        revision.setArticleId(rs.getInt("article_id"));
        revision.setAuthorId(rs.getInt("author_id"));
        revision.setChanges(rs.getString("changes"));
        revision.setRevisedAt(rs.getTimestamp("revised_at"));
        return revision;
    }
}