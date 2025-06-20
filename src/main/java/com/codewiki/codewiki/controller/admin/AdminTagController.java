package com.codewiki.codewiki.controller.admin;

import com.codewiki.codewiki.model.Tag;
import com.codewiki.codewiki.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/tags")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTagController {

    private final TagService tagService;

    @Autowired
    public AdminTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String listTags(Model model) {
        model.addAttribute("tags", tagService.getAllTags());
        return "admin/tags";
    }

    @GetMapping("/new")
    public String showTagForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin/tag_form";
    }

    @GetMapping("/edit/{id}")
    public String showEditTagForm(@PathVariable Long id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Tag tag = tagService.getTagById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
            model.addAttribute("tag", tag);
            return "admin/tag_form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tags";
        }
    }

    @PostMapping("/save")
    public String saveTag(@ModelAttribute Tag tag,
                         RedirectAttributes redirectAttributes) {
        try {
            Optional<Tag> existingTag = tagService.findByName(tag.getName());
            if (existingTag.isPresent() && !existingTag.get().getId().equals(tag.getId())) {
                throw new IllegalArgumentException("Tag with this name already exists");
            }
            
            tagService.saveTag(tag);
            redirectAttributes.addFlashAttribute("success", "Tag saved successfully");
            return "redirect:/admin/tags";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tags/new";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTag(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            tagService.deleteTag(id);
            redirectAttributes.addFlashAttribute("success", "Tag deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tags";
    }
}