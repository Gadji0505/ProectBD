<?php if (!empty($article)): ?>
    <div class="article-view">
        <h1 class="article-title"><?php echo htmlspecialchars($article['title']); ?></h1>
        <div class="article-meta">
            Опубликовано: <?php echo date('d.m.Y H:i', strtotime($article['created_at'])); ?> автором <a href="/profile/<?php echo $article['author_id']; ?>"><?php echo htmlspecialchars($article['author_name']); ?></a>
            <?php if ($article['created_at'] !== $article['updated_at']): ?>
                (Обновлено: <?php echo date('d.m.Y H:i', strtotime($article['updated_at'])); ?>)
            <?php endif; ?>
            <span class="article-status">Статус: <strong><?php echo htmlspecialchars(ucfirst($article['status'])); ?></strong></span>
        </div>

        <div class="article-actions">
            <?php // Пример: Если пользователь авторизован и имеет право на редактирование/удаление ?>
            <?php if (isset($_SESSION['user_id']) && ($_SESSION['user_id'] == $article['author_id'] || (isset($_SESSION['user_role']) && $_SESSION['user_role'] === 'admin'))): ?>
                <a href="/article/<?php echo htmlspecialchars($article['slug']); ?>/edit" class="btn btn-edit">Редактировать</a>
                <form action="/article/<?php echo htmlspecialchars($article['slug']); ?>/delete" method="POST" style="display: inline-block;" onsubmit="return confirm('Вы уверены, что хотите удалить эту статью?');">
                    <button type="submit" class="btn btn-delete">Удалить</button>
                </form>
            <?php endif; ?>
        </div>

        <div class="article-content">
            <?php
            // $article['content_html'] уже должен быть подготовлен Parsedown'ом в ArticleController@show
            echo $article['content_html'];
            ?>
        </div>

        <div class="article-comments">
            <h3>Комментарии</h3>
            <p>Функционал комментариев будет добавлен позже.</p>
            <?php // Пример формы для добавления комментария (только для авторизованных) ?>
            <?php if (isset($_SESSION['user_id'])): ?>
                <form action="/article/<?php echo htmlspecialchars($article['slug']); ?>/comment" method="POST">
                    <div class="form-group">
                        <label for="comment_content">Ваш комментарий:</label>
                        <textarea id="comment_content" name="content" rows="5" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Добавить комментарий</button>
                </form>
            <?php else: ?>
                <p><a href="/login">Войдите</a>, чтобы оставить комментарий.</p>
            <?php endif; ?>
        </div>
    </div>
<?php else: ?>
    <div class="alert error">
        <h1 class="page-title">Статья не найдена.</h1>
        <p>К сожалению, статья, которую вы ищете, не существует или была удалена.</p>
        <p><a href="/articles">Вернуться к списку статей</a></p>
    </div>
<?php endif; ?>

<style>
    /* Дополнительные стили для view.php, можно перенести в style.css */
    .article-view {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        line-height: 1.7;
    }

    .article-title {
        color: #0056b3;
        margin-bottom: 10px;
        font-size: 2.2em;
    }

    .article-meta {
        font-size: 0.9em;
        color: #777;
        margin-bottom: 20px;
        border-bottom: 1px solid #eee;
        padding-bottom: 15px;
    }

    .article-meta a {
        color: #007bff;
        text-decoration: none;
    }

    .article-meta a:hover {
        text-decoration: underline;
    }

    .article-status {
        margin-left: 15px;
        padding: 4px 8px;
        background-color: #e0f2f7;
        border-radius: 4px;
        color: #0056b3;
        font-weight: normal;
    }

    .article-actions {
        margin-bottom: 25px;
        border-bottom: 1px solid #eee;
        padding-bottom: 15px;
    }

    .btn {
        padding: 8px 15px;
        border-radius: 5px;
        text-decoration: none;
        color: white;
        font-weight: bold;
        transition: background-color 0.2s ease;
        display: inline-block;
        margin-right: 10px;
    }

    .btn-edit {
        background-color: #007bff;
    }

    .btn-edit:hover {
        background-color: #0056b3;
    }

    .btn-delete {
        background-color: #dc3545;
        border: none; /* Убрать рамку для кнопки submit */
        cursor: pointer;
    }

    .btn-delete:hover {
        background-color: #c82333;
    }

    .article-content h1, .article-content h2, .article-content h3, .article-content h4, .article-content h5, .article-content h6 {
        color: #333;
        margin-top: 1.5em;
        margin-bottom: 0.8em;
    }

    .article-content p {
        margin-bottom: 1em;
    }

    .article-content pre {
        background-color: #282c34; /* Темный фон для блока кода */
        color: #abb2bf; /* Светлый текст */
        padding: 1em;
        border-radius: 5px;
        overflow-x: auto; /* Горизонтальный скролл для длинных строк */
        margin-bottom: 1em;
    }

    .article-content code {
        font-family: 'Fira Code', 'Cascadia Code', 'Consolas', monospace; /* Моноширинный шрифт для кода */
        font-size: 0.9em;
    }

    .article-comments {
        margin-top: 40px;
        padding-top: 30px;
        border-top: 1px solid #eee;
    }

    .article-comments h3 {
        color: #0056b3;
        margin-bottom: 20px;
    }

    .article-comments .form-group {
        margin-bottom: 15px;
    }

    .article-comments textarea {
        width: 100%;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 1em;
        min-height: 100px;
        resize: vertical;
    }

    .article-comments .btn-primary {
        background-color: #28a745;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1em;
        transition: background-color 0.2s ease;
    }

    .article-comments .btn-primary:hover {
        background-color: #218838;
    }
</style>