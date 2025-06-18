<h1 class="page-title">404 - Страница не найдена</h1>

<div class="error-container">
    <p>Ой! Похоже, вы заблудились. Запрашиваемая вами страница не существует.</p>
    <p>Вот несколько вариантов, что вы можете сделать:</p>
    <ul>
        <li><a href="/">Вернуться на главную страницу</a></li>
        <li>Проверить правильность введенного URL-адреса.</li>
        <li>Использовать поиск по сайту, если он доступен.</li>
    </ul>
    <img src="/public/images/404.png" alt="Страница не найдена" class="error-image">
</div>

<style>
    /* Стили для страницы ошибки 404, можно перенести в style.css */
    .error-container {
        text-align: center;
        background-color: #fff;
        padding: 40px;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        max-width: 600px;
        margin: 50px auto;
    }

    .error-container h1 {
        color: #dc3545; /* Красный цвет для ошибки */
        font-size: 3em;
        margin-bottom: 20px;
    }

    .error-container p {
        font-size: 1.1em;
        color: #555;
        margin-bottom: 15px;
    }

    .error-container ul {
        list-style: none;
        padding: 0;
        margin-top: 25px;
        margin-bottom: 30px;
    }

    .error-container ul li {
        margin-bottom: 10px;
        color: #666;
    }

    .error-container ul li a {
        color: #007bff;
        text-decoration: none;
        font-weight: bold;
    }

    .error-container ul li a:hover {
        text-decoration: underline;
    }

    .error-image {
        max-width: 100%;
        height: auto;
        margin-top: 30px;
        border-radius: 5px;
        opacity: 0.8;
    }
</style>