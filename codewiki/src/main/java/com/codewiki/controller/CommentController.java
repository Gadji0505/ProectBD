package com.codewiki.controller;

import com.codewiki.dao.CommentDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.UserDao;
import com.codewiki.model.Comment;
import com.codewiki.model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Date;

@WebServlet("/comments/*")
public class CommentController extends HttpServlet {
    private final CommentDao commentDao = new CommentDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo() != null ? request.getPathInfo() : "/";

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        switch (action) {
            case "/add":
                addComment(request, response, userId);
                break;
            case "/delete":
                deleteComment(request, response, userId);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response, int userId) throws IOException, ServletException {
        int articleId = Integer.parseInt(request.getParameter("articleId"));
        String content = request.getParameter("content");

        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("error", "Comment cannot be empty");
            Article article = articleDao.getArticleById(articleId);
            request.setAttribute("article", article);
            request.getRequestDispatcher("/templates/article/view.html").forward(request, response);
            return;
        }

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setAuthorId(userId);
        comment.setContent(content);
        comment.setCreatedAt(new Date());

        commentDao.createComment(comment);

        response.sendRedirect(request.getContextPath() + "/articles/view?id=" + articleId);
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response, int userId) throws IOException {
        int commentId = Integer.parseInt(request.getParameter("id"));
        Comment comment = commentDao.getCommentById(commentId);
        User currentUser = userDao.getUserById(userId);

        if (comment != null && (comment.getAuthorId() == userId || currentUser.isAdmin())) {
            int articleId = comment.getArticleId();
            commentDao.deleteComment(commentId);
            response.sendRedirect(request.getContextPath() + "/articles/view?id=" + articleId);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}