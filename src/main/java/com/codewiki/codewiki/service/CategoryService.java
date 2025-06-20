package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Category;
import com.codewiki.codewiki.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlugIgnoreCase(slug);
    }

    @Transactional
    public Category saveCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        
        // Проверка уникальности имени
        Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(category.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }

        if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
            category.setSlug(generateSlug(category.getName()));
        } else {
            // Проверка уникальности slug
            Optional<Category> existingSlug = categoryRepository.findBySlugIgnoreCase(category.getSlug());
            if (existingSlug.isPresent() && !existingSlug.get().getId().equals(category.getId())) {
                throw new IllegalArgumentException("Category with slug '" + category.getSlug() + "' already exists");
            }
        }
        
        return categoryRepository.save(category);
    }

    @Transactional
    public Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return saveCategory(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (countArticlesInCategory(id) > 0) {
            throw new IllegalStateException("Cannot delete category with articles");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Category> getPopularCategories(int limit) {
        return categoryRepository.findPopularCategories(limit);
    }

    @Transactional(readOnly = true)
    public long countArticlesInCategory(Long categoryId) {
        return categoryRepository.countArticlesByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public long getTotalCategoriesCount() {
        return categoryRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategories(String query) {
        return categoryRepository.searchByName(query);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlugIgnoreCase(slug);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    @Transactional
    public Category findOrCreateCategory(String name) {
        return findByName(name)
                .orElseGet(() -> createCategory(name));
    }
}