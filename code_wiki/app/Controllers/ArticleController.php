<?php

namespace App\Controllers;

use App\Core\View;
use App\Core\Database; // Возможно, понадобится для взаимодействия с БД
use App\Models\Article;
use Parsedown; // Для обработки Markdown

class ArticleController
{
    protected Database $db; // Добавляем свойство для базы данных

    public function __construct()
    {
        // Получаем экземпляр базы данных через глобальный объект, или из DI-контейнера в реальных проектах
        // Пока что, для простоты, предположим, что Application сделал его доступным
        global $app; // Доступ к экземпляру Application
        $this->db = $app->getDatabase(); // Предположим, что в Application есть метод getDatabase()
    }

    /**
     * Отображает список всех статей (или с пагинацией).
     */
    public function index(): void
    {
        // Пример получения статей:
        $articleModel = new Article($this->db);
        $articles = $articleModel->getAllPublished(); // Получаем только опубликованные статьи

        View::render('article/index', [ // Предполагаем, что есть представление article/index.php
            'title' => 'Все статьи CodeWiki',
            'articles' => $articles
        ]);
    }

    /**
     * Отображает конкретную статью по ее slug.
     *
     * @param string $slug Slug статьи.
     */
    public function show(string $slug): void
    {
        $articleModel = new Article($this->db);
        $article = $articleModel->findBySlug($slug);

        if (!$article) {
            // Если статья не найдена, показываем 404
            http_response_code(404);
            View::render('errors/404', ['title' => 'Статья не найдена']);
            return;
        }

        // Парсим Markdown контент
        $Parsedown = new Parsedown();
        $article['content_html'] = $Parsedown->text($article['content']);

        View::render('article/view', [
            'title' => $article['title'],
            'article' => $article
        ]);
    }

    /**
     * Отображает форму для создания новой статьи.
     */
    public function create(): void
    {
        // Проверка авторизации: только авторизованные пользователи могут создавать статьи
        // if (!isLoggedIn()) { redirectTo('/login'); }
        View::render('article/create', [
            'title' => 'Создать новую статью'
        ]);
    }

    /**
     * Обрабатывает POST-запрос на сохранение новой статьи.
     */
    public function store(): void
    {
        // Проверка авторизации и CSRF-токен
        // if (!isLoggedIn() || !validateCsrfToken()) { redirectTo('/login'); }

        // Получаем данные из POST-запроса
        $title = $_POST['title'] ?? '';
        $content = $_POST['content'] ?? '';
        $status = $_POST['status'] ?? 'draft'; // По умолчанию черновик

        // Валидация данных (проверка на пустоту, длину и т.д.)
        if (empty($title) || empty($content)) {
            // Можно перенаправить обратно с сообщением об ошибке
            // header('Location: /articles/create?error=empty_fields');
            // exit;
        }

        $articleModel = new Article($this->db);
        // Предполагаем, что у вас есть ID текущего пользователя
        $authorId = 1; // Замените на реальный ID пользователя из сессии

        $result = $articleModel->createArticle($title, $content, $authorId, $status);

        if ($result) {
            // Успешно создано, перенаправляем на страницу новой статьи или список
            header('Location: /article/' . $result['slug']); // result['slug'] должен быть возвращен после создания
            exit;
        } else {
            // Ошибка сохранения
            // header('Location: /articles/create?error=save_failed');
            // exit;
        }
    }

    /**
     * Отображает форму для редактирования существующей статьи.
     *
     * @param string $slug Slug статьи.
     */
    public function edit(string $slug): void
    {
        // Проверка авторизации и прав на редактирование
        // if (!isLoggedIn() || !canEditArticle($slug, userId())) { redirectTo('/login'); }

        $articleModel = new Article($this->db);
        $article = $articleModel->findBySlug($slug);

        if (!$article) {
            http_response_code(404);
            View::render('errors/404', ['title' => 'Статья не найдена']);
            return;
        }

        View::render('article/edit', [
            'title' => 'Редактировать статью: ' . $article['title'],
            'article' => $article
        ]);
    }

    /**
     * Обрабатывает POST-запрос на обновление существующей статьи.
     *
     * @param string $slug Slug статьи.
     */
    public function update(string $slug): void
    {
        // Проверка авторизации и прав на редактирование, CSRF-токен
        // if (!isLoggedIn() || !canEditArticle($slug, userId()) || !validateCsrfToken()) { redirectTo('/login'); }

        $title = $_POST['title'] ?? '';
        $content = $_POST['content'] ?? '';
        $status = $_POST['status'] ?? 'draft';

        if (empty($title) || empty($content)) {
            // Handle error
        }

        $articleModel = new Article($this->db);
        $article = $articleModel->findBySlug($slug); // Получаем ID статьи по slug

        if (!$article) {
            http_response_code(404);
            View::render('errors/404', ['title' => 'Статья не найдена']);
            return;
        }

        $articleId = $article['id'];
        $updaterId = 1; // Замените на реальный ID пользователя из сессии

        $result = $articleModel->updateArticle($articleId, $title, $content, $status, $updaterId);

        if ($result) {
            header('Location: /article/' . $slug);
            exit;
        } else {
            // Handle error
        }
    }

    /**
     * Обрабатывает POST-запрос на удаление статьи.
     * Только для администраторов.
     *
     * @param string $slug Slug статьи.
     */
    public function delete(string $slug): void
    {
        // Проверка прав администратора и CSRF-токен
        // if (!isAdmin() || !validateCsrfToken()) { redirectTo('/admin/articles?error=permission_denied'); }

        $articleModel = new Article($this->db);
        $article = $articleModel->findBySlug($slug);

        if (!$article) {
            // Статья не найдена, но это может быть нормальным в случае удаления
            header('Location: /admin/articles?message=article_not_found_or_already_deleted');
            exit;
        }

        if ($articleModel->deleteArticle($article['id'])) {
            header('Location: /admin/articles?message=article_deleted');
            exit;
        } else {
            // Handle error
            header('Location: /admin/articles?error=deletion_failed');
            exit;
        }
    }
}