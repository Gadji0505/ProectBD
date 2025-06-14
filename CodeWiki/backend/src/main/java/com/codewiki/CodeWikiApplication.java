package com.codewiki;

import com.codewiki.models.User;
import com.codewiki.models.UserRole;
import com.codewiki.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing  // Для автоматического заполнения @CreationTimestamp и @UpdateTimestamp
public class CodeWikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeWikiApplication.class, args);
    }

    /**
     * Инициализация первого администратора при запуске.
     */
    @Bean
    CommandLineRunner initAdmin(
        UserRepository userRepo,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Проверяем, существует ли уже админ
            if (!userRepo.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@codewiki.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Смените пароль в продакшене!
                admin.setRole(UserRole.ADMIN);
                admin.setReputation(100); // Высокая репутация для админа
                userRepo.save(admin);
                System.out.println("Admin user created: admin / admin123");
            }
        };
    }

    /**
     * Дополнительная инициализация тестовых данных (опционально).
     */
    @Bean
    CommandLineRunner initTestData(UserRepository userRepo) {
        return args -> {
            if (userRepo.count() == 1) {  // Если только админ
                User testUser = new User();
                testUser.setUsername("editor");
                testUser.setEmail("editor@codewiki.com");
                testUser.setPassword(passwordEncoder.encode("editor123"));
                testUser.setRole(UserRole.EDITOR);
                userRepo.save(testUser);
            }
        };
    }
}