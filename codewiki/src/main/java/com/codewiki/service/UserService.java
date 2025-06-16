package com.codewiki.service;

import com.codewiki.dao.UserDao;
import com.codewiki.dao.ArticleDao;
import com.codewiki.dao.CommentDao;
import com.codewiki.model.User;
import com.codewiki.model.Article;
import com.codewiki.model.Comment;
import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final CommentDao commentDao = new CommentDao();

    public User getUserById(int id) {
        User user = userDao.getUserById(id);
        if (user != null) {
            user.setArticles(articleDao.getArticlesByAuthor(id));
            user.setComments(commentDao.getCommentsByAuthor(id));
        }
        return user;
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public boolean createUser(User user) {
        return userDao.createUser(user);
    }

    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    public boolean deleteUser(int id) {
        // Проверяем, есть ли у пользователя статьи или комментарии
        List<Article> articles = articleDao.getArticlesByAuthor(id);
        List<Comment> comments = commentDao.getCommentsByAuthor(id);
        
        if (!articles.isEmpty() || !comments.isEmpty()) {
            return false; // Не удаляем пользователя, если есть связанные данные
        }
        return userDao.deleteUser(id);
    }

    public boolean updateUserReputation(int userId, int reputationChange) {
        return userDao.updateReputation(userId, reputationChange);
    }

    public List<Article> getUserArticles(int userId) {
        return articleDao.getArticlesByAuthor(userId);
    }

    public List<Comment> getUserComments(int userId) {
        return commentDao.getCommentsByAuthor(userId);
    }

    public int getUserArticleCount(int userId) {
        return articleDao.getArticlesByAuthor(userId).size();
    }

    public int getUserCommentCount(int userId) {
        return commentDao.getCommentsByAuthor(userId).size();
    }

    public boolean isEmailExists(String email) {
        return userDao.getUserByEmail(email) != null;
    }

    public boolean isUsernameExists(String username) {
        return userDao.getUserByUsername(username) != null;
    }
}