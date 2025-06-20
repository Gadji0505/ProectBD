package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Role;
import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.service.RoleService;
import com.codewiki.codewiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Проверка на уникальность email
        Optional<User> existingUserByEmail = userService.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Пользователь с таким email уже существует.");
            return "redirect:/register";
        }

        // Проверка на уникальность username
        Optional<User> existingUserByUsername = userService.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Пользователь с таким именем пользователя уже существует.");
            return "redirect:/register";
        }

        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Установка роли по умолчанию
        Optional<Role> userRoleOptional = roleService.findByName("ROLE_USER");
        if (userRoleOptional.isEmpty()) {
            Role userRole = new Role("ROLE_USER");
            userRole = roleService.save(userRole);
            userRoleOptional = Optional.of(userRole);
        }

        Set<Role> roles = new HashSet<>();
        userRoleOptional.ifPresent(roles::add);
        user.setRoles(roles);

        // Инициализация репутации
        user.setReputation(0);

        userService.createUser(user);

        redirectAttributes.addFlashAttribute("message", "Регистрация успешна! Теперь вы можете войти.");
        return "redirect:/login";
    }
}