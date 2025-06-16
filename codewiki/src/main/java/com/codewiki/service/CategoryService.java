package com.codewiki.service;

import com.codewiki.dao.CategoryDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.model.Category;
import com.codewiki.model.Article;
import java.util.List;

public class CategoryService {
    private final CategoryDao categoryDao = new CategoryDao();
    private final ArticleDao articleDao = new ArticleDao();

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    public Category getCategoryBySlug(String slug) {
        return categoryDao.getCategoryBySlug(slug);
    }

    public boolean createCategory(Category category) {
        return categoryDao.createCategory(category);
    }

    public boolean updateCategory(Category category) {
        return categoryDao.updateCategory(category);
    }

    public boolean deleteCategory(int id) {
        // Проверяем, есть ли статьи в этой категории
        List<Article> articles = articleDao.getArticlesByCategory(id);
        if (!articles.isEmpty()) {
            return false; // Не удаляем категорию, если есть связанные статьи
        }
        return categoryDao.deleteCategory(id);
    }

    public List<Article> getArticlesForCategory(int categoryId) {
        return articleDao.getArticlesByCategory(categoryId);
    }

    public int getArticleCountForCategory(int categoryId) {
        return articleDao.getArticlesByCategory(categoryId).size();
    }
}