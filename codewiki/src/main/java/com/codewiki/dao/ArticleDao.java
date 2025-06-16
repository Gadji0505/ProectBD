package com.codewiki.dao;

import com.codewiki.model.Article;
import com.codewiki.model.Category;
import com.codewiki.model.Tag;
import com.codewiki.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDao {
    public List<Article> getAllPublishedArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM articles WHERE status = 'published' ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                articles.add(mapArticleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public Article getArticleById(int id) {
        String sql = "SELECT * FROM articles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Article article = mapArticleFromResultSet(rs);
                    article.setCategories(getArticleCategories(id));
                    article.setTags(getArticleTags(id));
                    return article;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Article getArticleBySlug(String slug) {
        String sql = "SELECT * FROM articles WHERE slug = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, slug);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Article article = mapArticleFromResultSet(rs);
                    article.setCategories(getArticleCategories(article.getId()));
                    article.setTags(getArticleTags(article.getId()));
                    return article;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createArticle(Article article) {
        String sql = "INSERT INTO articles (title, slug, content, author_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getSlug());
            pstmt.setString(3, article.getContent());
            pstmt.setInt(4, article.getAuthorId());
            pstmt.setString(5, article.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating article failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateArticle(Article article) {
        String sql = "UPDATE articles SET title = ?, slug = ?, content = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getSlug());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getStatus());
            pstmt.setInt(5, article.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteArticle(int id) {
        String sql = "DELETE FROM articles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Category> getArticleCategories(int articleId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.* FROM categories c JOIN article_categories ac ON c.id = ac.category_id WHERE ac.article_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    category.setSlug(rs.getString("slug"));
                    category.setDescription(rs.getString("description"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Tag> getArticleTags(int articleId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t JOIN article_tags at ON t.id = at.tag_id WHERE at.article_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tag tag = new Tag();
                    tag.setId(rs.getInt("id"));
                    tag.setName(rs.getString("name"));
                    tags.add(tag);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public boolean addArticleCategory(int articleId, int categoryId) {
        String sql = "INSERT INTO article_categories (article_id, category_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            pstmt.setInt(2, categoryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addArticleTag(int articleId, int tagId) {
        String sql = "INSERT INTO article_tags (article_id, tag_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            pstmt.setInt(2, tagId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearArticleCategories(int articleId) {
        String sql = "DELETE FROM article_categories WHERE article_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearArticleTags(int articleId) {
        String sql = "DELETE FROM article_tags WHERE article_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, articleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Article mapArticleFromResultSet(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setSlug(rs.getString("slug"));
        article.setContent(rs.getString("content"));
        article.setAuthorId(rs.getInt("author_id"));
        article.setCreatedAt(rs.getTimestamp("created_at"));
        article.setUpdatedAt(rs.getTimestamp("updated_at"));
        article.setStatus(rs.getString("status"));
        return article;
    }
}