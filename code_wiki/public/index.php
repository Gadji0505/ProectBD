<?php

// public/index.php

// Определяем константу для корневой директории проекта
// __DIR__ возвращает директорию текущего файла (public/)
// dirname(__DIR__) поднимается на один уровень выше, в корневую директорию code_wiki/
define('ROOT_PATH', dirname(__DIR__));

// Подключаем автозагрузчик Composer
// Это позволит автоматически загружать классы из папки app/
require_once ROOT_PATH . '/vendor/autoload.php';

// Подключаем файл конфигурации
$config = require_once ROOT_PATH . '/app/Config/config.php';

// Создаем экземпляр класса Application и запускаем его
// Namespace App\Core соответствует структуре app/Core
use App\Core\Application;

$app = new Application($config);
$app->run();