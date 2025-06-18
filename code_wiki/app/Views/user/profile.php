<?php if (!empty($user)): ?>
    <div class="user-profile">
        <h1 class="page-title">Профиль пользователя: <?php echo htmlspecialchars($user['name']); ?></h1>

        <div class="profile-details">
            <div class="detail-item">
                <strong>Имя пользователя:</strong> <?php echo htmlspecialchars($user['name']); ?>
            </div>
            <div class="detail-item">
                <strong>Email:</strong> <?php echo htmlspecialchars($user['email']); ?>
            </div>
            <div class="detail-item">
                <strong>Репутация:</strong> <?php echo (int)$user['reputation']; ?>
            </div>
            <div class="detail-item">
                <strong>Зарегистрирован:</strong> <?php echo date('d.m.Y H:i', strtotime($user['registration_date'])); ?>
            </div>
            <div class="detail-item">
                <strong>Роль:</strong> <?php echo htmlspecialchars(ucfirst($user['role'])); ?>
            </div>
        </div>

        <?php // Возможно, здесь будут кнопки для редактирования профиля, если это текущий пользователь ?>
        <?php if (isset($_SESSION['user_id']) && $_SESSION['user_id'] == $user['id']): ?>
            <div class="profile-actions">
                <a href="/profile/<?php echo $user['id']; ?>/edit" class="btn btn-edit">Редактировать профиль</a>
                </div>
        <?php endif; ?>

        <section class="user-articles">
            <h2>Статьи пользователя</h2>
            <?php if (!empty($articles) && is_array($articles)): // Предполагается, что $articles тоже может быть передан ?>
                <div class="article-list">
                    <?php foreach ($articles as $article): ?>
                        <div class="article-card">
                            <h3><a href="/article/<?php echo htmlspecialchars($article['slug']); ?>"><?php echo htmlspecialchars($article['title']); ?></a></h3>
                            <p class="article-meta">Опубликовано: <?php echo date('d.m.Y', strtotime($article['created_at'])); ?></p>
                            <p><?php echo htmlspecialchars(substr($article['content'], 0, 150)); ?>...</p>
                            <a href="/article/<?php echo htmlspecialchars($article['slug']); ?>" class="read-more">Читать далее</a>
                        </div>
                    <?php endforeach; ?>
                </div>
            <?php else: ?>
                <p><?php echo htmlspecialchars($user['name']); ?> пока не опубликовал ни одной статьи.</p>
            <?php endif; ?>
        </section>

    </div>
<?php else: ?>
    <div class="alert error">
        <h1 class="page-title">Пользователь не найден.</h1>
        <p>К сожалению, профиль пользователя, которого вы ищете, не существует.</p>
        <p><a href="/">Вернуться на главную</a></p>
    </div>
<?php endif; ?>

<style>
    /* Дополнительные стили для profile.php, можно перенести в style.css */
    .user-profile {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        max-width: 800px;
        margin: 20px auto;
    }

    .user-profile .page-title {
        color: #0056b3;
        margin-bottom: 25px;
        text-align: center;
    }

    .profile-details {
        margin-bottom: 30px;
        border: 1px solid #eee;
        border-radius: 8px;
        padding: 20px;
        background-color: #fcfcfc;
    }

    .profile-details .detail-item {
        margin-bottom: 10px;
        font-size: 1.1em;
        line-height: 1.5;
        border-bottom: 1px dashed #f0f0f0;
        padding-bottom: 8px;
    }

    .profile-details .detail-item:last-child {
        border-bottom: none;
        padding-bottom: 0;
    }

    .profile-details strong {
        color: #333;
        display: inline-block;
        width: 150px; /* Фиксированная ширина для выравнивания */
    }

    .profile-actions {
        margin-bottom: 40px;
        text-align: center;
    }

    .btn {
        padding: 10px 20px;
        border-radius: 5px;
        text-decoration: none;
        color: white;
        font-weight: bold;
        transition: background-color 0.2s ease;
        display: inline-block;
        margin: 0 10px;
    }

    .btn-edit {
        background-color: #007bff;
    }

    .btn-edit:hover {
        background-color: #0056b3;
    }

    .btn-secondary {
        background-color: #6c757d;
    }

    .btn-secondary:hover {
        background-color: #5a6268;
    }

    .user-articles {
        margin-top: 40px;
        padding-top: 30px;
        border-top: 1px solid #eee;
    }

    .user-articles h2 {
        color: #0056b3;
        margin-bottom: 20px;
        text-align: center;
    }

    .article-list {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 25px;
    }

    .article-card {
        background-color: #fefefe;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.03);
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }

    .article-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 15px rgba(0,0,0,0.08);
    }

    .article-card h3 {
        margin-top: 0;
        margin-bottom: 10px;
        font-size: 1.4em;
    }

    .article-card h3 a {
        color: #007bff;
        text-decoration: none;
    }

    .article-card h3 a:hover {
        text-decoration: underline;
    }

    .article-card .article-meta {
        font-size: 0.85em;
        color: #888;
        margin-bottom: 15px;
    }

    .article-card p {
        font-size: 0.95em;
        line-height: 1.6;
        color: #555;
        margin-bottom: 15px;
    }

    .article-card .read-more {
        display: inline-block;
        margin-top: 10px;
        color: #007bff;
        text-decoration: none;
        font-weight: bold;
    }

    .article-card .read-more:hover {
        text-decoration: underline;
    }

    .alert {
        padding: 15px;
        margin-bottom: 20px;
        border-radius: 5px;
        font-weight: bold;
        text-align: center;
    }

    .alert.error {
        background-color: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }
</style>