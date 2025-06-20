package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.service.UserService;
import com.codewiki.codewiki.service.ArticleService;
import com.codewiki.codewiki.service.CommentService;
import com.codewiki.codewiki.service.RevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;
    private final RevisionService revisionService;

    @Autowired
    public ProfileController(UserService userService,
                           ArticleService articleService,
                           CommentService commentService,
                           RevisionService revisionService) {
        this.userService = userService;
        this.articleService = articleService;
        this.commentService = commentService;
        this.revisionService = revisionService;
    }

    @GetMapping
    public String viewProfile(Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(redirectAttributes);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("userArticles", articleService.getArticlesByAuthor(user.getUsername()));
        model.addAttribute("userComments", commentService.getCommentsByAuthor(user));
        model.addAttribute("userRevisions", revisionService.getRevisionsByEditorId(user.getId()));
        model.addAttribute("stats", userService.getUserStats(user.getId()));

        return "profile/view";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(redirectAttributes);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(redirectAttributes);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "profile/edit";
        }

        if (!currentUser.getUsername().equals(updatedUser.getUsername())) {
            if (userService.existsByUsername(updatedUser.getUsername())) {
                result.rejectValue("username", "error.user", "Username already exists");
                return "profile/edit";
            }
        }

        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setBio(updatedUser.getBio());
        currentUser.setAvatarUrl(updatedUser.getAvatarUrl());

        userService.updateUser(currentUser);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(redirectAttributes);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("passwordForm", new PasswordChangeForm());
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(redirectAttributes);
        if (user == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "profile/change-password";
        }

        if (!userService.checkPassword(user, form.getOldPassword())) {
            result.rejectValue("oldPassword", "error.passwordForm", "Incorrect current password");
            return "profile/change-password";
        }

        userService.changePassword(user.getId(), form.getNewPassword());
        redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        return "redirect:/profile";
    }

    private User getCurrentUser(RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in");
            return null;
        }

        return userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private static class PasswordChangeForm {
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}