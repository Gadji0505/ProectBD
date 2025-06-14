package com.codewiki.services;

import com.codewiki.models.User;
import com.codewiki.models.UserRole;
import com.codewiki.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public User findById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User banUser(Long id) {
        User user = findById(id);
        user.setRole(UserRole.BANNED);
        return userRepo.save(user);
    }

    public User changeRole(Long id, UserRole role) {
        User user = findById(id);
        user.setRole(role);
        return userRepo.save(user);
    }
}