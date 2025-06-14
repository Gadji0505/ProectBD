package com.codewiki.repositories;

import com.codewiki.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Найти тег по имени (уникальное поле)
    Optional<Tag> findByName(String name);

    // Проверить, существует ли тег с таким именем
    boolean existsByName(String name);
}