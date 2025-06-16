package com.codewiki.controller;

import com.codewiki.dao.RevisionDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.UserDao;
import com.codewiki.model.Revision;
import com.codewiki.model.Article;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/revisions/*")
public class RevisionController extends HttpServlet {
    private final RevisionDao revisionDao = new RevisionDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        switch (action) {
            case "/list":
                listRevisions(request, response);
                break;
            case "/view":
                viewRevision(request, response);
                break;
            case "/restore":
                restoreRevision(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listRevisions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int articleId = Integer.parseInt(request.getParameter("articleId"));
        List<Revision> revisions = revisionDao.getRevisionsByArticle(articleId);
        
        request.setAttribute("revisions", revisions);
        request.setAttribute("article", articleDao.getArticleById(articleId));
        request.getRequestDispatcher("/templates/revision/list.html").forward(request, response);
    }

    private void viewRevision(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int revisionId = Integer.parseInt(request.getParameter("id"));
        Revision revision = revisionDao.getRevisionById(revisionId);
        
        if (revision != null) {
            request.setAttribute("revision", revision);
            request.setAttribute("article", articleDao.getArticleById(revision.getArticleId()));
            request.setAttribute("author", userDao.getUserById(revision.getAuthorId()));
            request.getRequestDispatcher("/templates/revision/view.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void restoreRevision(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        int revisionId = Integer.parseInt(request.getParameter("id"));
        Revision revision = revisionDao.getRevisionById(revisionId);
        
        if (revision != null) {
            Article article = articleDao.getArticleById(revision.getArticleId());
            article.setContent(revision.getChanges());
            article.setUpdatedAt(new Date());
            articleDao.updateArticle(article);
            
            // Создаем новую ревизию при восстановлении
            Revision newRevision = new Revision();
            newRevision.setArticleId(article.getId());
            newRevision.setAuthorId(userId);
            newRevision.setChanges("Restored from revision #" + revisionId);
            newRevision.setRevisedAt(new Date());
            revisionDao.createRevision(newRevision);
        }
        
        response.sendRedirect(request.getContextPath() + "/articles/view?id=" + revision.getArticleId());
    }
}