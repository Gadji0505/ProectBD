package com.codewiki.service;

import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.CategoryDao;
import com.codewiki.dao.TagDao;
import com.codewiki.model.Article;
import com.codewiki.model.Category;
import com.codewiki.model.Tag;
import java.util.List;

public class ArticleService {
    private final ArticleDao articleDao = new ArticleDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final TagDao tagDao = new TagDao();

    public List<Article> getAllPublishedArticles() {
        return articleDao.getAllPublishedArticles();
    }

    public Article getArticleById(int id) {
        Article article = articleDao.getArticleById(id);
        if (article != null) {
            article.setCategories(categoryDao.getCategoriesForArticle(id));
            article.setTags(tagDao.getTagsForArticle(id));
        }
        return article;
    }

    public Article getArticleBySlug(String slug) {
        Article article = articleDao.getArticleBySlug(slug);
        if (article != null) {
            article.setCategories(categoryDao.getCategoriesForArticle(article.getId()));
            article.setTags(tagDao.getTagsForArticle(article.getId()));
        }
        return article;
    }

    public boolean createArticle(Article article, List<Integer> categoryIds, List<Integer> tagIds) {
        int articleId = articleDao.createArticle(article);
        if (articleId > 0) {
            addCategoriesToArticle(articleId, categoryIds);
            addTagsToArticle(articleId, tagIds);
            return true;
        }
        return false;
    }

    public boolean updateArticle(Article article, List<Integer> categoryIds, List<Integer> tagIds) {
        if (articleDao.updateArticle(article)) {
            articleDao.clearArticleCategories(article.getId());
            articleDao.clearArticleTags(article.getId());
            addCategoriesToArticle(article.getId(), categoryIds);
            addTagsToArticle(article.getId(), tagIds);
            return true;
        }
        return false;
    }

    public boolean deleteArticle(int id) {
        return articleDao.deleteArticle(id);
    }

    private void addCategoriesToArticle(int articleId, List<Integer> categoryIds) {
        if (categoryIds != null) {
            for (int categoryId : categoryIds) {
                articleDao.addArticleCategory(articleId, categoryId);
            }
        }
    }

    private void addTagsToArticle(int articleId, List<Integer> tagIds) {
        if (tagIds != null) {
            for (int tagId : tagIds) {
                articleDao.addArticleTag(articleId, tagId);
            }
        }
    }

    public List<Article> getArticlesByCategory(int categoryId) {
        return articleDao.getArticlesByCategory(categoryId);
    }

    public List<Article> getArticlesByTag(int tagId) {
        return articleDao.getArticlesByTag(tagId);
    }
}