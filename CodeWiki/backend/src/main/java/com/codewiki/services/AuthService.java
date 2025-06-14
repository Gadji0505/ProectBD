package com.codewiki.services;

import com.codewiki.dto.AuthRequest;
import com.codewiki.dto.AuthResponse;
import com.codewiki.dto.RegisterRequest;
import com.codewiki.models.User;
import com.codewiki.models.UserRole;
import com.codewiki.repositories.UserRepository;
import com.codewiki.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authManager;

    public AuthResponse login(AuthRequest request) {
        // Аутентификация через Spring Security
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepo.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token);
    }

    public User register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.EDITOR); // По умолчанию — обычный редактор
        return userRepo.save(user);
    }
}