<?php

namespace App\Controllers;

use App\Core\View;
use App\Core\Database;
use App\Models\User;
use App\Models\Article;
use App\Models\Report;

class AdminController
{
    protected Database $db;

    public function __construct()
    {
        global $app;
        $this->db = $app->getDatabase();

        // **Важно:** Реализовать здесь проверку прав администратора!
        // if (!isset($_SESSION['user_role']) || $_SESSION['user_role'] !== 'admin') {
        //     header('Location: /login'); // Или на страницу с ошибкой доступа
        //     exit;
        // }
    }

    /**
     * Отображает главную страницу админ-панели (дашборд).
     */
    public function dashboard(): void
    {
        View::render('admin/dashboard', [
            'title' => 'Админ-панель'
        ]);
    }

    /**
     * Управляет пользователями: список, удаление.
     */
    public function manageUsers(): void
    {
        $userModel = new User($this->db);
        $users = $userModel->getAll();

        View::render('admin/users', [
            'title' => 'Управление пользователями',
            'users' => $users
        ]);
    }

    /**
     * Удаляет пользователя.
     *
     * @param int $id ID пользователя.
     */
    public function deleteUser(int $id): void
    {
        // Проверка CSRF-токена
        // if (!validateCsrfToken()) { redirectTo('/admin/users?error=invalid_token'); }

        $userModel = new User($this->db);
        if ($userModel->deleteUser($id)) {
            header('Location: /admin/users?message=user_deleted');
            exit;
        } else {
            header('Location: /admin/users?error=deletion_failed');
            exit;
        }
    }

    /**
     * Управляет статьями: список, удаление.
     */
    public function manageArticles(): void
    {
        $articleModel = new Article($this->db);
        $articles = $articleModel->getAll(); // Получаем все статьи, включая черновики и архивы

        View::render('admin/articles', [
            'title' => 'Управление статьями',
            'articles' => $articles
        ]);
    }

    // Метод deleteArticle уже есть в ArticleController,
    // но администратор может использовать его напрямую.
    // Возможно, стоит перенести всю логику удаления в AdminController,
    // или добавить проверку роли в ArticleController->delete.
    // Пока оставим, что ArticleController->delete вызывается из AdminController.

    /**
     * Управляет жалобами.
     */
    public function manageReports(): void
    {
        $reportModel = new Report($this->db);
        $reports = $reportModel->getAllPendingReports(); // Получаем только нерассмотренные жалобы

        View::render('admin/reports', [
            'title' => 'Управление жалобами',
            'reports' => $reports
        ]);
    }

    /**
     * Отмечает жалобу как рассмотренную.
     *
     * @param int $id ID жалобы.
     */
    public function resolveReport(int $id): void
    {
        // Проверка CSRF-токена
        // if (!validateCsrfToken()) { redirectTo('/admin/reports?error=invalid_token'); }

        $reportModel = new Report($this->db);
        if ($reportModel->resolveReport($id)) {
            header('Location: /admin/reports?message=report_resolved');
            exit;
        } else {
            header('Location: /admin/reports?error=resolution_failed');
            exit;
        }
    }
}