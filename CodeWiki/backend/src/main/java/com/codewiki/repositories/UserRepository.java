package com.codewiki.repositories;

import com.codewiki.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Найти пользователя по email (уникальное поле)
    Optional<User> findByEmail(String email);

    // Найти пользователя по username (уникальное поле)
    Optional<User> findByUsername(String username);

    // Проверить, существует ли пользователь с таким email
    boolean existsByEmail(String email);

    // Проверить, существует ли пользователь с таким username
    boolean existsByUsername(String username);
}