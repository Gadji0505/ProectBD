package com.codewiki.repositories;

import com.codewiki.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Найти категорию по имени (уникальное поле)
    Optional<Category> findByName(String name);

    // Проверить, существует ли категория с таким именем
    boolean existsByName(String name);
}