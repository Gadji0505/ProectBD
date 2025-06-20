package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Category;
import com.codewiki.codewiki.model.Tag;
import com.codewiki.codewiki.service.ArticleService;
import com.codewiki.codewiki.service.CategoryService;
import com.codewiki.codewiki.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final int LATEST_ARTICLES_COUNT = 5;
    private static final int POPULAR_CATEGORIES_COUNT = 6;
    private static final int POPULAR_TAGS_COUNT = 10;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    @GetMapping("/")
    public String home(Model model,
                      @RequestParam(required = false, defaultValue = "" + DEFAULT_PAGE_NUMBER) int page,
                      @RequestParam(required = false, defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        
        try {
            // Получаем последние статьи с пагинацией
            Pageable pageable = PageRequest.of(
                validatePageNumber(page), 
                validatePageSize(size), 
                Sort.by("createdAt").descending()
            );
            
            Page<?> latestArticlesPage = articleService.getLatestArticles(pageable);
            model.addAttribute("latestArticles", latestArticlesPage);
            addPaginationAttributes(model, latestArticlesPage);
            
            // Получаем популярные статьи (по количеству просмотров)
            model.addAttribute("popularArticles", 
                articleService.getPopularArticles(LATEST_ARTICLES_COUNT));
            
            // Получаем популярные категории (по количеству статей)
            List<Category> popularCategories = 
                categoryService.getPopularCategories(POPULAR_CATEGORIES_COUNT);
            model.addAttribute("popularCategories", popularCategories);
            
            // Получаем популярные теги (по частоте использования)
            List<Tag> popularTags = 
                tagService.getPopularTags(POPULAR_TAGS_COUNT);
            model.addAttribute("popularTags", popularTags);

            // Статистика для главной страницы
            model.addAttribute("totalArticles", articleService.getTotalArticlesCount());
            model.addAttribute("totalCategories", categoryService.getTotalCategoriesCount());
            model.addAttribute("totalTags", tagService.getTotalTagsCount());

        } catch (Exception e) {
            log.error("Error loading home page data", e);
            model.addAttribute("errorMessage", "Error loading page data. Please try again later.");
        }

        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam String query,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) Set<Long> tagIds,
                        @RequestParam(required = false, defaultValue = "" + DEFAULT_PAGE_NUMBER) int page,
                        @RequestParam(required = false, defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                        Model model) {
        
        try {
            Pageable pageable = PageRequest.of(
                validatePageNumber(page), 
                validatePageSize(size)
            );
            
            // Поиск статей по комплексному запросу с пагинацией
            Page<?> searchResults = articleService.findComplexSearch(
                query, categoryId, tagIds, pageable);
            
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("searchQuery", query);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedTagIds", tagIds);
            
            addPaginationAttributes(model, searchResults);
            
            // Добавляем списки категорий и тегов для фильтрации
            model.addAttribute("allCategories", categoryService.getAllCategories());
            model.addAttribute("allTags", tagService.getAllTags());

        } catch (Exception e) {
            log.error("Search error for query: {}", query, e);
            model.addAttribute("errorMessage", "Search error. Please try again.");
        }

        return "search-results";
    }

    // Вспомогательные методы
    private void addPaginationAttributes(Model model, Page<?> page) {
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("hasNext", page.hasNext());
        model.addAttribute("hasPrevious", page.hasPrevious());
    }

    private int validatePageNumber(int page) {
        return Math.max(page, 0);
    }

    private int validatePageSize(int size) {
        return size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, 50);
    }
}