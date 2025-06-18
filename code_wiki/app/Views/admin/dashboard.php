<h1 class="page-title"><?php echo $title ?? 'Админ-панель'; ?></h1>

<div class="admin-dashboard">
    <p>Добро пожаловать в админ-панель, <?php echo htmlspecialchars($_SESSION['user_name'] ?? 'Администратор'); ?>!</p>
    <p>Здесь вы можете управлять пользователями, статьями и другим контентом сайта.</p>

    <div class="dashboard-widgets">
        <div class="widget">
            <h3>Управление пользователями</h3>
            <p>Просматривайте, редактируйте и удаляйте учетные записи пользователей.</p>
            <a href="/admin/users" class="btn btn-primary">Перейти к пользователям</a>
        </div>

        <div class="widget">
            <h3>Управление статьями</h3>
            <p>Редактируйте, публикуйте, архивируйте или удаляйте статьи.</p>
            <a href="/admin/articles" class="btn btn-primary">Перейти к статьям</a>
        </div>

        <div class="widget">
            <h3>Управление категориями</h3>
            <p>Добавляйте, редактируйте и удаляйте категории статей.</p>
            <a href="/admin/categories" class="btn btn-primary">Перейти к категориям</a>
        </div>

        <div class="widget">
            <h3>Управление тегами</h3>
            <p>Добавляйте, редактируйте и удаляйте теги статей.</p>
            <a href="/admin/tags" class="btn btn-primary">Перейти к тегам</a>
        </div>

        <div class="widget">
            <h3>Отчеты о нарушениях</h3>
            <p>Просматривайте и разрешайте жалобы на статьи, комментарии или пользователей.</p>
            <a href="/admin/reports" class="btn btn-primary">Просмотреть отчеты</a>
            <?php if (isset($pendingReportsCount) && $pendingReportsCount > 0): ?>
                <span class="badge"><?php echo (int)$pendingReportsCount; ?></span>
            <?php endif; ?>
        </div>

        <div class="widget">
            <h3>Системная информация</h3>
            <p>Посмотреть информацию о PHP и сервере.</p>
            <a href="#" class="btn btn-secondary" onclick="alert('Эта функция пока не реализована.'); return false;">Подробнее</a>
        </div>
    </div>
</div>

<style>
    /* Стили для админ-панели, можно перенести в style.css */
    .admin-dashboard {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        max-width: 1200px;
        margin: 20px auto;
    }

    .admin-dashboard .page-title {
        color: #0056b3;
        margin-bottom: 25px;
        text-align: center;
    }

    .admin-dashboard p {
        text-align: center;
        margin-bottom: 30px;
        font-size: 1.1em;
        color: #555;
    }

    .dashboard-widgets {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 25px;
        margin-top: 30px;
    }

    .widget {
        background-color: #fefefe;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 25px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.03);
        text-align: center;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }

    .widget:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 15px rgba(0,0,0,0.08);
    }

    .widget h3 {
        color: #007bff;
        margin-top: 0;
        margin-bottom: 15px;
        font-size: 1.5em;
    }

    .widget p {
        font-size: 0.95em;
        color: #666;
        flex-grow: 1; /* Позволяет параграфу занимать доступное пространство */
        margin-bottom: 20px;
        text-align: center;
    }

    .widget .btn {
        display: inline-block;
        padding: 10px 20px;
        background-color: #007bff;
        color: white;
        text-decoration: none;
        border-radius: 5px;
        font-weight: bold;
        transition: background-color 0.2s ease;
        margin-top: auto; /* Прижимает кнопку к низу виджета */
    }

    .widget .btn:hover {
        background-color: #0056b3;
    }

    .widget .btn-secondary {
        background-color: #6c757d;
    }

    .widget .btn-secondary:hover {
        background-color: #5a6268;
    }

    .badge {
        display: inline-block;
        padding: 5px 10px;
        background-color: #dc3545; /* Красный цвет для уведомлений */
        color: white;
        border-radius: 50%;
        font-size: 0.8em;
        font-weight: bold;
        margin-left: 10px;
        vertical-align: top; /* Выравнивание по верху */
    }
</style>