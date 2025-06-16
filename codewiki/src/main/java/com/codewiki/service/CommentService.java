package com.codewiki.service;

import com.codewiki.dao.CommentDao;
import com.codewiki.dao.UserDao;
import com.codewiki.model.Comment;
import com.codewiki.model.User;
import java.util.List;

public class CommentService {
    private final CommentDao commentDao = new CommentDao();
    private final UserDao userDao = new UserDao();

    public List<Comment> getCommentsByArticle(int articleId) {
        List<Comment> comments = commentDao.getCommentsByArticle(articleId);
        for (Comment comment : comments) {
            User author = userDao.getUserById(comment.getAuthorId());
            comment.setAuthor(author);
        }
        return comments;
    }

    public Comment getCommentById(int id) {
        Comment comment = commentDao.getCommentById(id);
        if (comment != null) {
            User author = userDao.getUserById(comment.getAuthorId());
            comment.setAuthor(author);
        }
        return comment;
    }

    public boolean createComment(Comment comment) {
        return commentDao.createComment(comment);
    }

    public boolean updateComment(Comment comment) {
        return commentDao.updateComment(comment);
    }

    public boolean deleteComment(int id) {
        return commentDao.deleteComment(id);
    }

    public boolean canUserDeleteComment(int commentId, int userId, boolean isAdmin) {
        Comment comment = getCommentById(commentId);
        return comment != null && (comment.getAuthorId() == userId || isAdmin);
    }

    public int getCommentCountForArticle(int articleId) {
        return commentDao.getCommentsByArticle(articleId).size();
    }
}