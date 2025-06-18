<h1 class="page-title">Добро пожаловать в CodeWiki!</h1>
<p>Ваша база знаний по программированию. Здесь вы найдете статьи, туториалы и советы от сообщества разработчиков.</p>

<section class="latest-articles">
    <h2>Последние статьи</h2>
    <?php if (!empty($articles)): ?>
        <div class="article-list">
            <?php foreach ($articles as $article): ?>
                <div class="article-card">
                    <h3><a href="/article/<?php echo htmlspecialchars($article['slug']); ?>"><?php echo htmlspecialchars($article['title']); ?></a></h3>
                    <p class="article-meta">Опубликовано: <?php echo date('d.m.Y', strtotime($article['created_at'])); ?> автором: <?php echo htmlspecialchars($article['author_name']); ?></p>
                    <p><?php echo htmlspecialchars(substr($article['content'], 0, 150)); ?>...</p> <a href="/article/<?php echo htmlspecialchars($article['slug']); ?>" class="read-more">Читать далее</a>
                </div>
            <?php endforeach; ?>
        </div>
    <?php else: ?>
        <p>Пока нет опубликованных статей. Будьте первым, кто внесет свой вклад!</p>
        <?php if (isset($_SESSION['user_id'])): ?>
            <p><a href="/articles/create">Создайте свою первую статью сейчас!</a></p>
        <?php else: ?>
            <p><a href="/register">Зарегистрируйтесь</a>, чтобы начать создавать статьи.</p>
        <?php endif; ?>
    <?php endif; ?>
</section>