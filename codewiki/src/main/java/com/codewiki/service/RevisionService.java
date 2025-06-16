package com.codewiki.service;

import com.codewiki.dao.RevisionDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.UserDao;
import com.codewiki.model.Revision;
import java.util.List;

public class RevisionService {
    private final RevisionDao revisionDao = new RevisionDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final UserDao userDao = new UserDao();

    public List<Revision> getRevisionsByArticle(int articleId) {
        List<Revision> revisions = revisionDao.getRevisionsByArticle(articleId);
        for (Revision revision : revisions) {
            revision.setArticle(articleDao.getArticleById(revision.getArticleId()));
            revision.setAuthor(userDao.getUserById(revision.getAuthorId()));
        }
        return revisions;
    }

    public Revision getRevisionById(int id) {
        Revision revision = revisionDao.getRevisionById(id);
        if (revision != null) {
            revision.setArticle(articleDao.getArticleById(revision.getArticleId()));
            revision.setAuthor(userDao.getUserById(revision.getAuthorId()));
        }
        return revision;
    }

    public boolean createRevision(Revision revision) {
        return revisionDao.createRevision(revision);
    }

    public boolean restoreRevision(int revisionId, int restoringUserId) {
        Revision revision = getRevisionById(revisionId);
        if (revision != null) {
            // Обновляем статью с содержимым из ревизии
            articleDao.updateArticleContent(revision.getArticleId(), revision.getChanges());

            // Создаем новую ревизию для записи факта восстановления
            Revision newRevision = new Revision();
            newRevision.setArticleId(revision.getArticleId());
            newRevision.setAuthorId(restoringUserId);
            newRevision.setChanges("Restored from revision #" + revisionId);
            return createRevision(newRevision);
        }
        return false;
    }

    public boolean deleteRevision(int id) {
        return revisionDao.deleteRevision(id);
    }

    public boolean deleteRevisionsForArticle(int articleId) {
        return revisionDao.deleteRevisionsForArticle(articleId);
    }

    public int getRevisionCountForArticle(int articleId) {
        return revisionDao.getRevisionsByArticle(articleId).size();
    }
}