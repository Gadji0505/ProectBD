<h1 class="page-title"><?php echo $title ?? 'Управление пользователями'; ?></h1>

<div class="admin-table-container">
    <?php if (isset($message)): ?>
        <div class="alert success"><?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>
    <?php if (isset($error)): ?>
        <div class="alert error"><?php echo htmlspecialchars($error); ?></div>
    <?php endif; ?>

    <?php if (!empty($users)): ?>
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Email</th>
                    <th>Роль</th>
                    <th>Репутация</th>
                    <th>Дата регистрации</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($users as $user): ?>
                    <tr>
                        <td><?php echo htmlspecialchars($user['id']); ?></td>
                        <td><a href="/profile/<?php echo htmlspecialchars($user['id']); ?>"><?php echo htmlspecialchars($user['name']); ?></a></td>
                        <td><?php echo htmlspecialchars($user['email']); ?></td>
                        <td><?php echo htmlspecialchars(ucfirst($user['role'])); ?></td>
                        <td><?php echo (int)$user['reputation']; ?></td>
                        <td><?php echo date('d.m.Y', strtotime($user['registration_date'])); ?></td>
                        <td>
                            <?php if ($user['id'] !== ($_SESSION['user_id'] ?? null)): // Нельзя удалить самого себя ?>
                                <form action="/admin/user/<?php echo htmlspecialchars($user['id']); ?>/delete" method="POST" style="display: inline;" onsubmit="return confirm('Вы уверены, что хотите удалить пользователя <?php echo htmlspecialchars($user['name']); ?>? Это действие необратимо и удалит весь его контент!');">
                                    <button type="submit" class="btn btn-danger btn-sm">Удалить</button>
                                </form>
                            <?php else: ?>
                                <span class="text-muted">Вы</span>
                            <?php endif; ?>
                        </td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    <?php else: ?>
        <div class="alert info">Пользователей пока нет.</div>
    <?php endif; ?>

    <div class="table-actions">
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

    .btn-danger {
        background-color: #dc3545;
    }

    .btn-danger:hover {
        background-color: #c82333;
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

    .text-muted {
        color: #888;
        font-style: italic;
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