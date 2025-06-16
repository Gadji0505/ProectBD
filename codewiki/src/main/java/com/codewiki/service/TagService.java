package com.codewiki.service;

import com.codewiki.dao.TagDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.model.Tag;
import com.codewiki.model.Article;
import java.util.List;

public class TagService {
    private final TagDao tagDao = new TagDao();
    private final ArticleDao articleDao = new ArticleDao();

    public List<Tag> getAllTags() {
        return tagDao.getAllTags();
    }

    public Tag getTagById(int id) {
        return tagDao.getTagById(id);
    }

    public Tag getTagByName(String name) {
        return tagDao.getTagByName(name);
    }

    public boolean createTag(Tag tag) {
        return tagDao.createTag(tag);
    }

    public boolean updateTag(Tag tag) {
        return tagDao.updateTag(tag);
    }

    public boolean deleteTag(int id) {
        // Проверяем, используется ли тег в статьях
        if (tagDao.isTagUsed(id)) {
            return false; // Не удаляем тег, если он используется
        }
        return tagDao.deleteTag(id);
    }

    public List<Article> getArticlesForTag(int tagId) {
        return articleDao.getArticlesByTag(tagId);
    }

    public int getArticleCountForTag(int tagId) {
        return articleDao.getArticlesByTag(tagId).size();
    }

    public List<Tag> getPopularTags(int limit) {
        return tagDao.getPopularTags(limit);
    }
}