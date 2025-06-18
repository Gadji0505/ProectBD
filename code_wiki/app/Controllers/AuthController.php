<?php

namespace App\Controllers;

use App\Core\View;
use App\Core\Database;
use App\Models\User;

class AuthController
{
    protected Database $db;

    public function __construct()
    {
        global $app;
        $this->db = $app->getDatabase();
    }

    /**
     * Отображает страницу входа.
     */
    public function login(): void
    {
        View::render('auth/login', [
            'title' => 'Вход'
        ]);
    }

    /**
     * Обрабатывает POST-запрос на аутентификацию пользователя.
     */
    public function authenticate(): void
    {
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';

        $userModel = new User($this->db);
        $user = $userModel->findByEmail($email);

        if ($user && password_verify($password, $user['password'])) {
            // Успешный вход
            $_SESSION['user_id'] = $user['id'];
            $_SESSION['user_name'] = $user['name'];
            $_SESSION['user_role'] = $user['role']; // Сохраняем роль пользователя

            header('Location: /'); // Перенаправляем на главную
            exit;
        } else {
            // Неудачная попытка входа
            View::render('auth/login', [
                'title' => 'Вход',
                'error' => 'Неверный email или пароль.'
            ]);
        }
    }

    /**
     * Отображает страницу регистрации.
     */
    public function register(): void
    {
        View::render('auth/register', [
            'title' => 'Регистрация'
        ]);
    }

    /**
     * Обрабатывает POST-запрос на регистрацию нового пользователя.
     */
    public function store(): void
    {
        $name = $_POST['name'] ?? '';
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        $passwordConfirm = $_POST['password_confirm'] ?? '';

        // Простая валидация
        if (empty($name) || empty($email) || empty($password) || empty($passwordConfirm)) {
            View::render('auth/register', [
                'title' => 'Регистрация',
                'error' => 'Все поля обязательны.'
            ]);
            return;
        }

        if ($password !== $passwordConfirm) {
            View::render('auth/register', [
                'title' => 'Регистрация',
                'error' => 'Пароли не совпадают.'
            ]);
            return;
        }

        $userModel = new User($this->db);

        // Проверка, существует ли пользователь с таким email
        if ($userModel->findByEmail($email)) {
            View::render('auth/register', [
                'title' => 'Регистрация',
                'error' => 'Пользователь с таким email уже существует.'
            ]);
            return;
        }

        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

        if ($userModel->createUser($name, $email, $hashedPassword)) {
            header('Location: /login?success=registered'); // Успешная регистрация
            exit;
        } else {
            View::render('auth/register', [
                'title' => 'Регистрация',
                'error' => 'Ошибка при регистрации. Пожалуйста, попробуйте еще раз.'
            ]);
        }
    }

    /**
     * Обрабатывает выход пользователя из системы.
     */
    public function logout(): void
    {
        session_start(); // Убедитесь, что сессия запущена
        session_unset(); // Удаляем все переменные сессии
        session_destroy(); // Уничтожаем сессию

        header('Location: /'); // Перенаправляем на главную
        exit;
    }
}