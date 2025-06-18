<?php

namespace App\Core;

class View
{
    /**
     * Рендерит (отображает) файл представления.
     *
     * @param string $view Имя файла представления (например, 'home/index').
     * @param array $data Ассоциативный массив данных, которые нужно передать в представление.
     */
    public static function render(string $view, array $data = []): void
    {
        // Создаем переменные из массива $data, чтобы они были доступны в представлении
        // Например, $data['title'] станет $title
        extract($data);

        // Формируем полный путь к файлу представления
        // Предполагаем, что представления находятся в 'app/Views/'
        $viewPath = ROOT_PATH . '/app/Views/' . str_replace('.', '/', $view) . '.php';

        if (file_exists($viewPath)) {
            // Подключаем заголовок (хедер)
            require_once ROOT_PATH . '/app/Views/layout/header.php';

            // Подключаем сам файл представления
            require_once $viewPath;

            // Подключаем футер
            require_once ROOT_PATH . '/app/Views/layout/footer.php';
        } else {
            // Если представление не найдено, можно выбросить исключение
            // Или показать страницу ошибки
            // Пока просто выведем ошибку
            die("Представление '$view' не найдено по пути: $viewPath");
        }
    }
}