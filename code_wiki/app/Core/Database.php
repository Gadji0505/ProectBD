<?php

namespace App\Core;

use PDO;
use PDOException;

class Database
{
    protected PDO $pdo;

    /**
     * Конструктор Database.
     * Устанавливает соединение с базой данных.
     *
     * @param array $config Массив с настройками подключения к БД.
     */
    public function __construct(array $config)
    {
        $dsn = "mysql:host={$config['host']};dbname={$config['name']};charset={$config['charset']}";
        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION, // Режим ошибок: исключения
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,       // Режим выборки по умолчанию: ассоциативный массив
            PDO::ATTR_EMULATE_PREPARES   => false,                  // Отключаем эмуляцию подготовленных запросов
        ];

        try {
            $this->pdo = new PDO($dsn, $config['user'], $config['password'], $options);
        } catch (PDOException $e) {
            // В продакшене лучше не выводить ошибку напрямую пользователю
            // А записать ее в лог и показать дружелюбное сообщение
            die("Ошибка подключения к базе данных: " . $e->getMessage());
        }
    }

    /**
     * Выполняет SQL-запрос.
     *
     * @param string $sql SQL-запрос.
     * @param array $params Массив параметров для подготовленного запроса.
     * @return \PDOStatement Возвращает объект PDOStatement.
     */
    public function query(string $sql, array $params = []): \PDOStatement
    {
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt;
    }

    /**
     * Возвращает ID последней вставленной строки.
     *
     * @return string ID последней вставленной строки.
     */
    public function lastInsertId(): string
    {
        return $this->pdo->lastInsertId();
    }
}