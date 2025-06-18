<?php

namespace App\Controllers;

use App\Core\View;
use App\Models\Article; // Для получения последних статей

class HomeController
{
    /**
     * Отображает главную страницу.
     */
    public function index(): void
    {
        // Пример: Получаем несколько последних статей для отображения на главной
        // Здесь мы пока не используем реальную базу данных,
        // но в будущем Article::getAll() будет получать данные из БД.
        $articles = []; // Пока пустой массив, но в будущем: Article::getAllLatest(5);

        View::render('home/index', [
            'title' => 'CodeWiki - База знаний для программистов',
            'articles' => $articles
        ]);
    }
}