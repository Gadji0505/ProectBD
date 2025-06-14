package com.codewiki.repositories;

import com.codewiki.models.Draft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {

    // Найти все черновики пользователя
    List<Draft> findByAuthorId(Long authorId);
}