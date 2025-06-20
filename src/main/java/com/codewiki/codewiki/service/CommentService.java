package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.model.Comment;
import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByArticle(Article article) {
        return commentRepository.findByArticleOrderByCreatedAtDesc(article);
    }

    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByAuthor(User author) {
        return commentRepository.findByAuthor(author);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return commentRepository.findAllByArticleIdOrderByCreatedAtDesc(articleId);
    }

    @Transactional(readOnly = true)
    public long countCommentsByArticle(Article article) {
        return commentRepository.countByArticle(article);
    }

    @Transactional
    public Comment saveComment(Comment comment) {
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment saveComment(Comment comment, Long articleId, String username) {
        return saveComment(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public void deleteCommentsByArticleId(Long articleId) {
        commentRepository.deleteAllByArticleId(articleId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findByAuthorIdAndArticleId(Long authorId, Long articleId) {
        return commentRepository.findByAuthorIdAndArticleIdOrderByCreatedAtDesc(authorId, articleId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getLatestComments() {
        return commentRepository.findTop5ByOrderByCreatedAtDesc();
    }
}