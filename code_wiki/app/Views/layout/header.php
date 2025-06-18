<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?php echo $title ?? 'CodeWiki'; ?></title>
    <link rel="stylesheet" href="/public/css/style.css">
    </head>
<body>
    <header>
        <nav>
            <div class="container">
                <a href="/" class="logo">CodeWiki</a>
                <ul>
                    <li><a href="/">Главная</a></li>
                    <li><a href="/articles">Статьи</a></li>
                    <li><a href="/categories">Категории</a></li>
                    <?php if (isset($_SESSION['user_id'])): ?>
                        <li><a href="/articles/create">Создать статью</a></li>
                        <li><a href="/profile/<?php echo $_SESSION['user_id']; ?>">Привет, <?php echo htmlspecialchars($_SESSION['user_name']); ?>!</a></li>
                        <?php if (isset($_SESSION['user_role']) && $_SESSION['user_role'] === 'admin'): ?>
                            <li><a href="/admin">Админ</a></li>
                        <?php endif; ?>
                        <li><a href="/logout">Выйти</a></li>
                    <?php else: ?>
                        <li><a href="/login">Войти</a></li>
                        <li><a href="/register">Регистрация</a></li>
                    <?php endif; ?>
                </ul>
            </div>
        </nav>
    </header>

    <main>
        <div class="container">
            <?php if (isset($_GET['message'])): ?>
                <div class="alert success">
                    <?php echo htmlspecialchars($_GET['message']); ?>
                </div>
            <?php endif; ?>
            <?php if (isset($_GET['error'])): ?>
                <div class="alert error">
                    <?php echo htmlspecialchars($_GET['error']); ?>
                </div>
            <?php endif; ?>
            