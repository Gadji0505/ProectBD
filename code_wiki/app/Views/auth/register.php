<h1 class="page-title"><?php echo $title ?? 'Регистрация'; ?></h1>

<div class="auth-form-container">
    <?php if (isset($error)): ?>
        <div class="alert error">
            <?php echo htmlspecialchars($error); ?>
        </div>
    <?php endif; ?>

    <form action="/register" method="POST" class="auth-form">
        <div class="form-group">
            <label for="name">Имя пользователя:</label>
            <input type="text" id="name" name="name" required autocomplete="username">
        </div>
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required autocomplete="email">
        </div>
        <div class="form-group">
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required autocomplete="new-password">
        </div>
        <div class="form-group">
            <label for="password_confirm">Подтвердите пароль:</label>
            <input type="password" id="password_confirm" name="password_confirm" required autocomplete="new-password">
        </div>
        <button type="submit" class="btn btn-primary">Зарегистрироваться</button>
    </form>

    <p class="auth-link">Уже есть аккаунт? <a href="/login">Войти</a></p>
</div>

<style>
    /* Эти стили можно также вынести в style.css, они похожи на стили для login.php */
    .auth-form-container {
        max-width: 400px;
        margin: 40px auto;
        padding: 30px;
        background-color: #f9f9f9;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        text-align: center;
    }

    .auth-form .form-group {
        margin-bottom: 20px;
        text-align: left;
    }

    .auth-form label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #555;
    }

    .auth-form input[type="text"],
    .auth-form input[type="email"],
    .auth-form input[type="password"] {
        width: calc(100% - 20px);
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 1em;
        box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
    }

    .auth-form input[type="text"]:focus,
    .auth-form input[type="email"]:focus,
    .auth-form input[type="password"]:focus {
        border-color: #007bff;
        outline: none;
        box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
    }

    .auth-form .btn {
        width: 100%;
        padding: 12px;
        border: none;
        border-radius: 5px;
        background-color: #28a745; /* Зеленая кнопка для регистрации */
        color: white;
        font-size: 1.1em;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .auth-form .btn:hover {
        background-color: #218838;
    }

    .auth-link {
        margin-top: 20px;
        font-size: 0.95em;
        color: #666;
    }

    .auth-link a {
        color: #007bff;
        text-decoration: none;
        font-weight: bold;
    }

    .auth-link a:hover {
        text-decoration: underline;
    }

    .alert {
        padding: 15px;
        margin-bottom: 20px;
        border-radius: 5px;
        font-weight: bold;
    }

    .alert.error {
        background-color: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }
</style>