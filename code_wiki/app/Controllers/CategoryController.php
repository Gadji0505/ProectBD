<?php

namespace App\Controllers;

use App\Core\View;
use App\Core\Database;
use App\Models\Category;
use App\Models\Article; // Для получения статей по категории

class CategoryController
{
    protected Database $db;

    public function __construct()
    {
        global $app;
        $this->db = $app->getDatabase();
    }

    /**
     * Отображает список всех категорий.
     */
    public function index(): void
    {
        $categoryModel = new Category($this->db);
        $categories = $categoryModel->getAll();

        View::render('category/index', [ // Предполагаем, что есть представление category/index.php
            'title' => 'Все категории',
            'categories' => $categories
        ]);
    }

    /**
     * Отображает статьи, относящиеся к определенной категории по ее slug.
     *
     * @param string $slug Slug категории.
     */
    public function show(string $slug): void
    {
        $categoryModel = new Category($this->db);
        $category = $categoryModel->findBySlug($slug);

        if (!$category) {
            http_response_code(404);
            View::render('errors/404', ['title' => 'Категория не найдена']);
            return;
        }

        $articleModel = new Article($this->db);
        $articles = $articleModel->getArticlesByCategory($category['id']); // Получаем статьи для этой категории

        View::render('category/show', [ // Предполагаем, что есть представление category/show.php
            'title' => 'Статьи в категории: ' . $category['name'],
            'category' => $category,
            'articles' => $articles
        ]);
    }

    // Методы для создания/редактирования/удаления категорий (возможно, в AdminController)
    // ...
}