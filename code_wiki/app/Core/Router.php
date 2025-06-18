<?php

namespace App\Core;

class Router
{
    protected array $routes = [];

    /**
     * Добавляет GET-маршрут.
     *
     * @param string $uri URI, который нужно сопоставить.
     * @param string $action Строка в формате "ControllerName@methodName".
     */
    public function get(string $uri, string $action): void
    {
        $this->addRoute('GET', $uri, $action);
    }

    /**
     * Добавляет POST-маршрут.
     *
     * @param string $uri URI, который нужно сопоставить.
     * @param string $action Строка в формате "ControllerName@methodName".
     */
    public function post(string $uri, string $action): void
    {
        $this->addRoute('POST', $uri, $action);
    }

    /**
     * Добавляет маршрут в массив маршрутов.
     *
     * @param string $method HTTP-метод (GET, POST).
     * @param string $uri URI маршрута.
     * @param string $action Действие (Controller@method).
     */
    protected function addRoute(string $method, string $uri, string $action): void
    {
        // Заменяем динамические сегменты URI на регулярные выражения
        // Например, {slug} превратится в ([a-zA-Z0-9-]+)
        $pattern = preg_replace('/\{([a-zA-Z0-9_]+)\}/', '([a-zA-Z0-9_-]+)', $uri);
        // Добавляем якоря для точного соответствия
        $pattern = '#^' . $pattern . '$#';
        $this->routes[$method][$pattern] = $action;
    }

    /**
     * Разрешает текущий запрос, находя и выполняя соответствующий контроллер.
     *
     * @param string $uri URI запроса.
     */
    public function resolve(string $uri): void
    {
        $method = $_SERVER['REQUEST_METHOD'];

        foreach ($this->routes[$method] as $pattern => $action) {
            if (preg_match($pattern, $uri, $matches)) {
                // Первый элемент $matches - это полный совпавший URI, нам нужны только группы
                array_shift($matches); // Удаляем полный URI

                [$controllerName, $methodName] = explode('@', $action);
                $controllerClass = "App\\Controllers\\" . $controllerName;

                if (class_exists($controllerClass)) {
                    $controller = new $controllerClass();
                    if (method_exists($controller, $methodName)) {
                        // Передаем динамические параметры в метод контроллера
                        call_user_func_array([$controller, $methodName], $matches);
                        return; // Маршрут найден и обработан, выходим
                    }
                }
            }
        }

        // Если маршрут не найден, показываем 404
        $this->show404();
    }

    /**
     * Отображает страницу 404 (Not Found).
     */
    protected function show404(): void
    {
        http_response_code(404);
        require ROOT_PATH . '/app/Views/errors/404.php';
        exit;
    }
}