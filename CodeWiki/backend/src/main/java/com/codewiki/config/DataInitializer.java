package com.codewiki.config;

import com.codewiki.models.User;
import com.codewiki.models.UserRole;
import com.codewiki.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
        UserRepository userRepo,
        PasswordEncoder encoder,
        @Value("${codewiki.admin.default-username}") String adminUsername,
        @Value("${codewiki.admin.default-password}") String adminPassword
    ) {
        return args -> {
            if (!userRepo.existsByUsername(adminUsername)) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);
                userRepo.save(admin);
            }
        };
    }
}