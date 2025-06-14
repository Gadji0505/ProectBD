package com.codewiki.repositories;

import com.codewiki.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Найти все комментарии к статье
    List<Comment> findByArticleId(Long articleId);

    // Найти все комментарии пользователя
    List<Comment> findByAuthorId(Long authorId);

    // Удалить все комментарии к статье (при удалении статьи)
    void deleteByArticleId(Long articleId);
}