package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.model.Comment;
import com.codewiki.codewiki.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleOrderByCreatedAtDesc(Article article);
    List<Comment> findByAuthor(User author);
    List<Comment> findAllByArticleId(Long articleId);
    long countByArticle(Article article);
    
    void deleteAllByArticleId(Long articleId);
    List<Comment> findByAuthorIdAndArticleIdOrderByCreatedAtDesc(Long authorId, Long articleId);
    List<Comment> findTop5ByOrderByCreatedAtDesc();
    long countByAuthor(User author);
    List<Comment> findAllByArticleIdOrderByCreatedAtDesc(Long articleId);
}