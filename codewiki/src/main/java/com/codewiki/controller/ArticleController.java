package com.codewiki.controller;

import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.CategoryDao;
import com.codewiki.dao.CommentDao;
import com.codewiki.dao.TagDao;
import com.codewiki.model.Article;
import com.codewiki.model.Comment;
import com.codewiki.util.MarkdownParser;
import com.codewiki.util.SlugGenerator;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/articles/*")
public class ArticleController extends HttpServlet {
    private final ArticleDao articleDao = new ArticleDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final TagDao tagDao = new TagDao();
    private final CommentDao commentDao = new CommentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        switch (action) {
            case "/":
                listArticles(request, response);
                break;
            case "/view":
                viewArticle(request, response);
                break;
            case "/create":
                showCreateForm(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        if (action.equals("/save")) {
            saveArticle(request, response);
        } else if (action.equals("/update")) {
            updateArticle(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listArticles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Article> articles = articleDao.getAllPublishedArticles();
        request.setAttribute("articles", articles);
        request.getRequestDispatcher("/templates/article/list.html").forward(request, response);
    }

    private void viewArticle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String slug = request.getParameter("slug");
        Article article = articleDao.getArticleBySlug(slug);
        
        if (article != null) {
            article.setContent(MarkdownParser.parse(article.getContent()));
            List<Comment> comments = commentDao.getCommentsByArticle(article.getId());
            
            request.setAttribute("article", article);
            request.setAttribute("comments", comments);
            request.getRequestDispatcher("/templates/article/view.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", categoryDao.getAllCategories());
        request.setAttribute("tags", tagDao.getAllTags());
        request.getRequestDispatcher("/templates/article/create.html").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Article article = articleDao.getArticleById(id);
        
        if (article != null) {
            request.setAttribute("article", article);
            request.setAttribute("categories", categoryDao.getAllCategories());
            request.setAttribute("tags", tagDao.getAllTags());
            request.getRequestDispatcher("/templates/article/edit.html").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void saveArticle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer authorId = (Integer) session.getAttribute("userId");
        
        if (authorId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Article article = new Article();
        article.setTitle(request.getParameter("title"));
        article.setContent(request.getParameter("content"));
        article.setSlug(SlugGenerator.generateSlug(article.getTitle()));
        article.setAuthorId(authorId);
        article.setCreatedAt(new Date());
        article.setStatus("published");

        int articleId = articleDao.createArticle(article);
        
        // Handle categories and tags
        String[] categories = request.getParameterValues("categories");
        String[] tags = request.getParameterValues("tags");
        
        if (categories != null) {
            for (String categoryId : categories) {
                articleDao.addArticleCategory(articleId, Integer.parseInt(categoryId));
            }
        }
        
        if (tags != null) {
            for (String tagId : tags) {
                articleDao.addArticleTag(articleId, Integer.parseInt(tagId));
            }
        }

        response.sendRedirect(request.getContextPath() + "/articles/view?slug=" + article.getSlug());
    }

    private void updateArticle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int articleId = Integer.parseInt(request.getParameter("id"));
        Article existingArticle = articleDao.getArticleById(articleId);
        
        if (existingArticle == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        existingArticle.setTitle(request.getParameter("title"));
        existingArticle.setContent(request.getParameter("content"));
        existingArticle.setUpdatedAt(new Date());
        
        articleDao.updateArticle(existingArticle);
        
        // Update categories and tags
        articleDao.clearArticleCategories(articleId);
        articleDao.clearArticleTags(articleId);
        
        String[] categories = request.getParameterValues("categories");
        String[] tags = request.getParameterValues("tags");
        
        if (categories != null) {
            for (String categoryId : categories) {
                articleDao.addArticleCategory(articleId, Integer.parseInt(categoryId));
            }
        }
        
        if (tags != null) {
            for (String tagId : tags) {
                articleDao.addArticleTag(articleId, Integer.parseInt(tagId));
            }
        }

        response.sendRedirect(request.getContextPath() + "/articles/view?slug=" + existingArticle.getSlug());
    }
}