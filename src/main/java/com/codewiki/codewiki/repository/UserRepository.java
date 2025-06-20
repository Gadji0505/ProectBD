package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Основные методы поиска
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    // Методы проверки существования
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Методы поиска
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    // Метод для поиска по email или username
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchByUsernameOrEmail(@Param("query") String query);
    
    // Методы для статистики
    @Query("SELECT COUNT(a) FROM Article a WHERE a.author.id = :userId")
    long countArticlesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author.id = :userId")
    long countCommentsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Revision r WHERE r.editor.id = :userId")
    long countRevisionsByUserId(@Param("userId") Long userId);
    
    // Методы для администратора
    List<User> findByEnabledFalse();
    
    // Метод для проверки пароля (используется только для проверки, не для получения)
    @Query("SELECT u.password FROM User u WHERE u.id = :userId")
    String findPasswordById(@Param("userId") Long userId);
}