<?php

namespace App\Controllers;

use App\Core\View;
use App\Core\Database;
use App\Models\User;

class UserController
{
    protected Database $db;

    public function __construct()
    {
        global $app;
        $this->db = $app->getDatabase();
    }

    /**
     * Отображает профиль пользователя по его ID.
     *
     * @param int $id ID пользователя.
     */
    public function show(int $id): void
    {
        $userModel = new User($this->db);
        $user = $userModel->findById($id);

        if (!$user) {
            http_response_code(404);
            View::render('errors/404', ['title' => 'Пользователь не найден']);
            return;
        }

        // Можно получить статьи, комментарии пользователя и т.д.
        // $userArticles = $userModel->getUserArticles($id);

        View::render('user/profile', [
            'title' => 'Профиль пользователя: ' . $user['name'],
            'user' => $user
            // 'articles' => $userArticles
        ]);
    }

    // Методы для редактирования профиля, изменения пароля и т.д.
    // ...
}