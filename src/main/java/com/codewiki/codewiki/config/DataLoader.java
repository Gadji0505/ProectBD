package com.codewiki.codewiki.config;

import com.codewiki.codewiki.model.*;
import com.codewiki.codewiki.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Profile("!test") // Не выполнять при тестах
public class DataLoader {

    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String USER_EMAIL = "user@example.com";
    private static final String MODERATOR_EMAIL = "moderator@example.com";

    @Bean
    @Transactional
    CommandLineRunner initDatabase(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 CategoryRepository categoryRepository,
                                 TagRepository tagRepository,
                                 ArticleRepository articleRepository,
                                 PasswordEncoder passwordEncoder) {
        return args -> {
            // Инициализация ролей
            Role userRole = initRole(roleRepository, "ROLE_USER", "Regular user");
            Role adminRole = initRole(roleRepository, "ROLE_ADMIN", "Administrator");
            Role moderatorRole = initRole(roleRepository, "ROLE_MODERATOR", "Content moderator");

            // Инициализация пользователей
            initUser(userRepository, passwordEncoder, "admin", ADMIN_EMAIL, "adminpass", 
                    "Admin User", 100, Set.of(adminRole, moderatorRole, userRole));
            initUser(userRepository, passwordEncoder, "moderator", MODERATOR_EMAIL, "modpass",
                    "Moderator User", 50, Set.of(moderatorRole, userRole));
            initUser(userRepository, passwordEncoder, "testuser", USER_EMAIL, "userpass",
                    "Test User", 10, Set.of(userRole));

            // Инициализация тестовых данных только для dev-профиля
            if (List.of(args).contains("--spring.profiles.active=dev")) {
                initTestData(categoryRepository, tagRepository, articleRepository, userRepository);
            }
        };
    }

    private Role initRole(RoleRepository roleRepository, String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(name)
                                .description(description)
                                .build()));
    }

    private void initUser(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         String username, 
                         String email,
                         String password,
                         String fullName,
                         int reputation,
                         Set<Role> roles) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .fullName(fullName)
                    .reputation(reputation)
                    .roles(roles)
                    .createdAt(LocalDateTime.now())
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }
    }

    private void initTestData(CategoryRepository categoryRepository,
                            TagRepository tagRepository,
                            ArticleRepository articleRepository,
                            UserRepository userRepository) {
        // Создание тестовых категорий
        Category programming = initCategory(categoryRepository, "Programming", "All about programming");
        Category algorithms = initCategory(categoryRepository, "Algorithms", "Algorithm explanations");
        
        // Создание тестовых тегов
        Tag javaTag = initTag(tagRepository, "java", "Java programming language");
        Tag springTag = initTag(tagRepository, "spring", "Spring Framework");
        
        // Создание тестовых статей
        Optional<User> adminOpt = userRepository.findByEmail(ADMIN_EMAIL);
        Optional<User> userOpt = userRepository.findByEmail(USER_EMAIL);
        
        adminOpt.ifPresent(admin -> {
            Article article = Article.builder()
                    .title("Getting Started with Spring Boot")
                    .content("Spring Boot makes it easy to create stand-alone Spring applications...")
                    .author(admin)
                    .category(programming)
                    .tags(Set.of(javaTag, springTag))
                    .build();
            articleRepository.save(article);
        });
    }

    private Category initCategory(CategoryRepository repository, String name, String description) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(
                        Category.builder()
                                .name(name)
                                .description(description)
                                .build()));
    }

    private Tag initTag(TagRepository repository, String name, String description) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(
                        Tag.builder()
                                .name(name)
                                .description(description)
                                .build()));
    }
}