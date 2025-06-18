<h1 class="page-title"><?php echo $title ?? 'Управление статьями'; ?></h1>

<div class="admin-table-container">
    <?php if (isset($message)): ?>
        <div class="alert success"><?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>
    <?php if (isset($error)): ?>
        <div class="alert error"><?php echo htmlspecialchars($error); ?></div>
    <?php endif; ?>

    <?php if (!empty($articles)): ?>
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Заголовок</th>
                    <th>Автор</th>
                    <th>Статус</th>
                    <th>Дата создания</th>
                    <th>Дата обновления</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($articles as $article): ?>
                    <tr>
                        <td><?php echo htmlspecialchars($article['id']); ?></td>
                        <td><a href="/article/<?php echo htmlspecialchars($article['slug']); ?>"><?php echo htmlspecialchars($article['title']); ?></a></td>
                        <td><a href="/profile/<?php echo htmlspecialchars($article['author_id']); ?>"><?php echo htmlspecialchars($article['author_name']); ?></a></td>
                        <td><span class="status-badge status-<?php echo htmlspecialchars($article['status']); ?>"><?php echo htmlspecialchars(ucfirst($article['status'])); ?></span></td>
                        <td><?php echo date('d.m.Y', strtotime($article['created_at'])); ?></td>
                        <td><?php echo date('d.m.Y', strtotime($article['updated_at'])); ?></td>
                        <td>
                            <a href="/article/<?php echo htmlspecialchars($article['slug']); ?>/edit" class="btn btn-info btn-sm">Редактировать</a>
                            <form action="/article/<?php echo htmlspecialchars($article['slug']); ?>/delete" method="POST" style="display: inline;" onsubmit="return confirm('Вы уверены, что хотите удалить статью «<?php echo htmlspecialchars($article['title']); ?>»?');">
                                <button type="submit" class="btn btn-danger btn-sm">Удалить</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    <?php else: ?>
        <div class="alert info">Статей пока нет.</div>
    <?php endif; ?>

    <div class="table-actions">
        <a href="/articles/create" class="btn btn-primary">Создать новую статью</a>
        <a href="/admin" class="btn btn-secondary">Назад к админ-панели</a>
    </div>
</div>

<style>
    /* Стили для таблиц админ-панели, можно перенести в style.css */
    .admin-table-container {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        max-width: 1200px;
        margin: 20px auto;
    }

    .admin-table-container .page-title {
        color: #0056b3;
        margin-bottom: 25px;
        text-align: center;
    }

    .data-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 25px;
    }

    .data-table th, .data-table td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: left;
        vertical-align: middle;
    }

    .data-table th {
        background-color: #f2f2f2;
        font-weight: bold;
        color: #333;
    }

    .data-table tr:nth-child(even) {
        background-color: #f9f9f9;
    }

    .data-table tr:hover {
        background-color: #f1f1f1;
    }

    .data-table td a {
        color: #007bff;
        text-decoration: none;
    }

    .data-table td a:hover {
        text-decoration: underline;
    }

    .btn {
        padding: 8px 15px;
        border-radius: 5px;
        text-decoration: none;
        color: white;
        font-weight: bold;
        transition: background-color 0.2s ease;
        border: none; /* Для кнопок submit */
        cursor: pointer;
    }

    .btn-sm {
        padding: 5px 10px;
        font-size: 0.85em;
    }

    .btn-info {
        background-color: #17a2b8; /* Сине-зеленый для информации/редактирования */
    }

    .btn-info:hover {
        background-color: #138496;
    }

    .btn-danger {
        background-color: #dc3545;
        margin-left: 5px; /* Небольшой отступ от кнопки редактирования */
    }

    .btn-danger:hover {
        background-color: #c82333;
    }

    .btn-primary {
        background-color: #007bff;
        color: white;
    }
    .btn-primary:hover {
        background-color: #0056b3;
    }

    .btn-secondary {
        background-color: #6c757d;
        color: white;
    }

    .btn-secondary:hover {
        background-color: #5a6268;
    }

    .table-actions {
        text-align: right;
        margin-top: 20px;
    }
    .table-actions .btn:first-child {
        float: left; /* Кнопка "Создать новую статью" слева */
    }

    .status-badge {
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 0.8em;
        font-weight: bold;
        color: white;
    }

    .status-badge.status-published {
        background-color: #28a745; /* Зеленый */
    }
    .status-badge.status-draft {
        background-color: #ffc107; /* Желтый */
        color: #343a40; /* Темный текст для желтого фона */
    }
    .status-badge.status-archived {
        background-color: #6c757d; /* Серый */
    }

    .alert {
        padding: 15px;
        margin-bottom: 20px;
        border-radius: 5px;
        font-weight: bold;
        text-align: center;
    }

    .alert.success {
        background-color: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
    }

    .alert.error {
        background-color: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }

    .alert.info {
        background-color: #d1ecf1;
        color: #0c5460;
        border: 1px solid #bee5eb;
    }
</style>