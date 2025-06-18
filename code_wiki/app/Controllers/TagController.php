<?php

namespace App\Controllers;

use App\Core\View;
use App\Core\Database;
use App\Models\Tag;
use App\Models\Article; // Для получения статей по тегу

class TagController
{
    protected Database $db;

    public function __construct()
    {
        global $app;
        $this->db = $app->getDatabase();
    }

    /**
     * Отображает список всех тегов.
     */
    public function index(): void
    {
        $tagModel = new Tag($this->db);
        $tags = $tagModel->getAll();

        View::render('tag/index', [ // Предполагаем, что есть представление tag/index.php
            'title' => 'Все теги',
            'tags' => $tags
        ]);
    }

    /**
     * Отображает статьи, относящиеся к определенному тегу по его названию (или slug).
     *
     * @param string $name Название тега.
     */
    public function show(string $name): void
    {
        $tagModel = new Tag($this->db);
        $tag = $tagModel->findByName($name); // Или findBySlug если теги имеют slug

        if (!$tag) {
            http_response_code(404);
            View::render('errors/404', ['title' => 'Тег не найден']);
            return;
        }

        $articleModel = new Article($this->db);
        $articles = $articleModel->getArticlesByTag($tag['id']); // Получаем статьи для этого тега

        View::render('tag/show', [ // Предполагаем, что есть представление tag/show.php
            'title' => 'Статьи с тегом: ' . $tag['name'],
            'tag' => $tag,
            'articles' => $articles
        ]);
    }

    // Методы для создания/редактирования/удаления тегов (возможно, в AdminController)
    // ...
}