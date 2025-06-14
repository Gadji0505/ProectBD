package com.codewiki.repositories;

import com.codewiki.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // Найти голос пользователя для статьи
    Optional<Vote> findByUserIdAndArticleId(Long userId, Long articleId);

    // Подсчитать количество лайков/дизлайков для статьи
    long countByArticleIdAndIsUpvote(Long articleId, boolean isUpvote);
}