<?php

namespace App\Core;

// Используем классы Router и Database
use App\Core\Router;
use App\Core\Database;

class Application
{
    protected Router $router;
    protected Database $db;
    protected array $config;

    /**
     * Конструктор Application.
     * Инициализирует конфигурацию, запускает сессию,
     * инициализирует базу данных и маршрутизатор.
     *
     * @param array $config Массив конфигурации приложения (из config.php).
     */
    public function __construct(array $config)
    {
        // Стартуем сессию в самом начале
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }

        $this->config = $config;
        
        // Инициализируем базу данных, передавая настройки из конфига
        $this->db = new Database($this->config['database']); 
        
        // Передаем экземпляр Application в глобальную область видимости
        // Это упрощает доступ к базе данных и другим сервисам из контроллеров
        // В более крупных проектах используют Service Locator или Dependency Injection Container
        global $app;
        $app = $this;

        $this->router = new Router();

        // Загружаем все маршруты приложения
        $this->loadRoutes();
    }

    /**
     * Запускает приложение.
     * Разбирает текущий URL и вызывает соответствующий контроллер/метод,
     * используя маршрутизатор.
     */
    public function run(): void
    {
        // Получаем текущий URL-путь из запроса
        // Используем parse_url для корректной обработки URI
        $requestUri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
        
        // Определяем базовый путь приложения, чтобы корректно маршрутизировать запросы
        // Например, если приложение доступно по http://localhost/code_wiki/public/,
        // то нам нужно отбросить /code_wiki/public/ из URI запроса.
        $baseUrlPath = parse_url($this->config['base_url'], PHP_URL_PATH);
        
        // Удаляем базовый путь из requestUri
        $path = str_replace($baseUrlPath, '', $requestUri);
        
        // Убедимся, что путь всегда начинается с '/', даже если он пуст после str_replace
        if (empty($path)) {
            $path = '/';
        } else {
            $path = '/' . ltrim($path, '/');
        }

        // Разрешаем маршрут через Router
        $this->router->resolve($path);
    }

    /**
     * Метод для получения экземпляра базы данных.
     * Используется контроллерами для доступа к БД.
     *
     * @return Database
     */
    public function getDatabase(): Database
    {
        return $this->db;
    }

    /**
     * Загружает все маршруты приложения.
     * Здесь определяются все URL-адреса и соответствующие им контроллеры и методы.
     */
    protected function loadRoutes(): void
    {
        // Главная страница
        $this->router->get('/', 'HomeController@index');

        // Аутентификация
        $this->router->get('/login', 'AuthController@login');
        $this->router->post('/login', 'AuthController@authenticate');
        $this->router->get('/register', 'AuthController@register');
        $this->router->post('/register', 'AuthController@store');
        $this->router->get('/logout', 'AuthController@logout');

        // Статьи
        $this->router->get('/articles', 'ArticleController@index'); // Список статей
        $this->router->get('/articles/create', 'ArticleController@create'); // Форма создания
        $this->router->post('/articles/store', 'ArticleController@store'); // Сохранение новой статьи
        $this->router->get('/article/{slug}', 'ArticleController@show'); // Просмотр статьи по slug
        $this->router->get('/article/{slug}/edit', 'ArticleController@edit'); // Форма редактирования
        $this->router->post('/article/{slug}/update', 'ArticleController@update'); // Обновление статьи
        $this->router->post('/article/{slug}/delete', 'ArticleController@delete'); // Удаление статьи

        // Категории
        $this->router->get('/categories', 'CategoryController@index'); // Список категорий
        $this->router->get('/category/{slug}', 'CategoryController@show'); // Статьи по категории

        // Пользователи
        $this->router->get('/profile/{id}', 'UserController@show'); // Профиль пользователя

        // Админ-панель (доступ только для администраторов)
        $this->router->get('/admin', 'AdminController@dashboard');
        $this->router->get('/admin/users', 'AdminController@manageUsers');
        $this->router->post('/admin/user/{id}/delete', 'AdminController@deleteUser');
        $this->router->get('/admin/articles', 'AdminController@manageArticles');
        // Обратите внимание: ArticleController@delete вызывается маршрутом.
        // Для админа может быть прямой маршрут на удаление по ID, без slug.
        // $this->router->post('/admin/article/{id}/delete', 'AdminController@deleteArticle'); 
        $this->router->get('/admin/reports', 'AdminController@manageReports');
        $this->router->post('/admin/report/{id}/resolve', 'AdminController@resolveReport');

        // Комментарии (могут быть AJAX-запросы)
        $this->router->post('/article/{slug}/comment', 'CommentController@store');

        // Жалобы (могут быть AJAX-запросы)
        $this->router->post('/report', 'ReportController@store');

        // Здесь можно добавлять другие маршруты по мере необходимости,
        // например, для тегов, ревизий, поиска и т.д.
        // $this->router->get('/tags', 'TagController@index');
        // $this->router->get('/tag/{name}', 'TagController@show');
    }
}