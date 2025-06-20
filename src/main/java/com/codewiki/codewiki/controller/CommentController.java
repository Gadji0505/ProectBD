package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Comment;
import com.codewiki.codewiki.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public String addComment(@RequestParam Long articleId,
                           @ModelAttribute Comment comment,
                           RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to comment");
            return "redirect:/login";
        }

        try {
            commentService.saveComment(comment, articleId, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Comment added successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding comment");
        }
        return "redirect:/articles/view/" + articleId;
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id,
                              @RequestParam Long articleId,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/login";
        }

        try {
            commentService.deleteComment(id);
            redirectAttributes.addFlashAttribute("success", "Comment deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting comment");
        }
        return "redirect:/articles/view/" + articleId;
    }
}