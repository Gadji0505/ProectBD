package com.codewiki.codewiki.controller.admin;

import com.codewiki.codewiki.model.Role;
import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.service.RoleService;
import com.codewiki.codewiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminUserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден.");
            return "redirect:/admin/users";
        }
        User user = userOptional.get();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        Set<Long> userRoleIds = user.getRoles().stream()
                                  .map(Role::getId)
                                  .collect(Collectors.toSet());
        model.addAttribute("userRoleIds", userRoleIds);
        return "admin/user_edit";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                         @RequestParam(value = "selectedRoles", required = false) List<Long> selectedRoleIds,
                         RedirectAttributes redirectAttributes) {
        Optional<User> existingUserOptional = userService.findById(user.getId());
        if (existingUserOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден для обновления.");
            return "redirect:/admin/users";
        }

        User existingUser = existingUserOptional.get();

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setReputation(user.getReputation());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        Set<Role> updatedRoles = new HashSet<>();
        if (selectedRoleIds != null) {
            for (Long roleId : selectedRoleIds) {
                roleService.getRoleById(roleId).ifPresent(updatedRoles::add);
            }
        }
        existingUser.setRoles(updatedRoles);

        userService.updateUser(existingUser);
        redirectAttributes.addFlashAttribute("message", "Пользователь успешно обновлен!");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Пользователь успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пользователя: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}