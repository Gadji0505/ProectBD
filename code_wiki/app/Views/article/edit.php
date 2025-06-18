<h1 class="page-title"><?php echo $title ?? 'Редактировать статью'; ?></h1>

<?php if (!empty($article)): ?>
    <div class="form-container">
        <?php if (isset($error)): ?>
            <div class="alert error">
                <?php echo htmlspecialchars($error); ?>
            </div>
        <?php endif; ?>

        <form action="/article/<?php echo htmlspecialchars($article['slug']); ?>/update" method="POST" class="article-form">
            <div class="form-group">
                <label for="title">Заголовок статьи:</label>
                <input type="text" id="title" name="title" required value="<?php echo htmlspecialchars($article['title']); ?>">
            </div>

            <div class="form-group">
                <label for="content">Содержимое статьи (Markdown):</label>
                <textarea id="content" name="content" rows="15" required><?php echo htmlspecialchars($article['content']); ?></textarea>
                <small>Используйте Markdown для форматирования текста (например, **жирный текст**, # Заголовок, `код`)</small>
            </div>

            <div class="form-group">
                <label for="status">Статус публикации:</label>
                <select id="status" name="status" required>
                    <option value="draft" <?php echo ($article['status'] === 'draft') ? 'selected' : ''; ?>>Черновик</option>
                    <option value="published" <?php echo ($article['status'] === 'published') ? 'selected' : ''; ?>>Опубликовано</option>
                    <option value="archived" <?php echo ($article['status'] === 'archived') ? 'selected' : ''; ?>>Архивировано</option>
                </select>
            </div>

            <div class="form-group">
                <label for="categories">Категории (ID через запятую, например: 1,2,3):</label>
                <?php
                // В реальном проекте здесь нужно получить текущие категории статьи
                // $currentCategoryIds = implode(',', $article['category_ids'] ?? []);
                $currentCategoryIds = ''; // Placeholder
                ?>
                <input type="text" id="categories" name="categories" value="<?php echo htmlspecialchars($currentCategoryIds); ?>" placeholder="Введите ID категорий">
                <small>В реальном приложении здесь будет удобный мультиселект.</small>
            </div>
            <div class="form-group">
                <label for="tags">Теги (разделяйте запятыми, например: php, web, database):</label>
                <?php
                // В реальном проекте здесь нужно получить текущие теги статьи
                // $currentTagNames = implode(',', array_column($article['tags'] ?? [], 'name'));
                $currentTagNames = ''; // Placeholder
                ?>
                <input type="text" id="tags" name="tags" value="<?php echo htmlspecialchars($currentTagNames); ?>" placeholder="Введите теги">
                <small>Теги будут созданы, если их еще нет.</small>
            </div>

            <button type="submit" class="btn btn-primary">Обновить статью</button>
        </form>
    </div>
<?php else: ?>
    <div class="alert error">
        <h1 class="page-title">Статья для редактирования не найдена.</h1>
        <p>К сожалению, статья, которую вы пытаетесь редактировать, не существует.</p>
        <p><a href="/articles">Вернуться к списку статей</a></p>
    </div>
<?php endif; ?>

<style>
    /* Стили для форм статей, можно перенести в style.css */
    .form-container {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        max-width: 900px;
        margin: 20px auto;
    }

    .article-form .form-group {
        margin-bottom: 20px;
    }

    .article-form label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #555;
    }

    .article-form input[type="text"],
    .article-form textarea,
    .article-form select {
        width: calc(100% - 22px); /* Учитываем padding и border */
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 1em;
    }

    .article-form textarea {
        resize: vertical;
    }

    .article-form input[type="text"]:focus,
    .article-form textarea:focus,
    .article-form select:focus {
        border-color: #007bff;
        outline: none;
        box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
    }

    .article-form small {
        display: block;
        margin-top: 5px;
        color: #888;
        font-size: 0.85em;
    }

    .article-form .btn {
        background-color: #007bff; /* Синяя кнопка для редактирования */
        color: white;
        padding: 12px 25px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1.1em;
        transition: background-color 0.2s ease;
    }

    .article-form .btn:hover {
        background-color: #0056b3;
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