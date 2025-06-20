package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Импортировать Authentication
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "Вы должны быть авторизованы для просмотра профиля.");
            return "redirect:/login";
        }

        String userEmail = authentication.getName(); // Spring Security по умолчанию использует username (или email, если так настроено в AuthService)
        Optional<User> userOptional = userService.findByEmail(userEmail); // Поиск пользователя по email, который используется как Principal

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            return "user/profile";
        } else {
            redirectAttributes.addFlashAttribute("error", "Профиль пользователя не найден.");
            // Это может произойти, если пользователь удален из БД, но сессия еще активна
            return "redirect:/login"; // Перенаправляем на страницу входа
        }
    }
}