<?php

namespace App\Models;

use App\Core\Database;
use DateTime;

class User
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Создает нового пользователя.
     *
     * @param string $name Имя пользователя.
     * @param string $email Email пользователя.
     * @param string $password Хэшированный пароль.
     * @param string $role Роль пользователя ('user' или 'admin').
     * @return int|false ID нового пользователя или false при ошибке.
     */
    public function createUser(string $name, string $email, string $password, string $role = 'user'): int|false
    {
        $now = (new DateTime())->format('Y-m-d H:i:s');
        $sql = "INSERT INTO users (name, email, password, reputation, registration_date, role)
                VALUES (:name, :email, :password, :reputation, :registration_date, :role)";
        $params = [
            ':name' => $name,
            ':email' => $email,
            ':password' => $password,
            ':reputation' => 0,
            ':registration_date' => $now,
            ':role' => $role
        ];

        try {
            $this->db->query($sql, $params);
            return (int)$this->db->lastInsertId();
        } catch (\PDOException $e) {
            error_log("Error creating user: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Находит пользователя по ID.
     *
     * @param int $id ID пользователя.
     * @return array|false Данные пользователя или false, если не найден.
     */
    public function findById(int $id): array|false
    {
        $sql = "SELECT id, name, email, reputation, registration_date, role FROM users WHERE id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->fetch();
    }

    /**
     * Находит пользователя по email.
     *
     * @param string $email Email пользователя.
     * @return array|false Данные пользователя или false, если не найден.
     */
    public function findByEmail(string $email): array|false
    {
        $sql = "SELECT id, name, email, password, reputation, registration_date, role FROM users WHERE email = :email";
        $stmt = $this->db->query($sql, [':email' => $email]);
        return $stmt->fetch();
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param int $id ID пользователя.
     * @param array $data Ассоциативный массив данных для обновления (например, ['name' => 'Новое Имя']).
     * @return bool Успех обновления.
     */
    public function updateUser(int $id, array $data): bool
    {
        $set = [];
        $params = [':id' => $id];
        foreach ($data as $key => $value) {
            $set[] = "$key = :$key";
            $params[":$key"] = $value;
        }
        if (empty($set)) return false;

        $sql = "UPDATE users SET " . implode(', ', $set) . " WHERE id = :id";
        try {
            $stmt = $this->db->query($sql, $params);
            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error updating user: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Удаляет пользователя по ID.
     * Также удаляет все связанные статьи, ревизии, комментарии и жалобы,
     * где этот пользователь является автором или репортером.
     * **Внимание:** Это каскадное удаление. В реальном проекте может быть лучше
     * "мягкое" удаление (изменение статуса) или перепривязка контента к "анонимному" пользователю.
     *
     * @param int $id ID пользователя.
     * @return bool Успех удаления.
     */
    public function deleteUser(int $id): bool
    {
        try {
            // Удаляем статьи, написанные этим пользователем
            $this->db->query("DELETE FROM articles WHERE author_id = :id", [':id' => $id]);

            // Удаляем ревизии, сделанные этим пользователем
            $this->db->query("DELETE FROM revisions WHERE author_id = :id", [':id' => $id]);

            // Удаляем комментарии, написанные этим пользователем
            $this->db->query("DELETE FROM comments WHERE author_id = :id", [':id' => $id]);

            // Удаляем жалобы, поданные этим пользователем
            $this->db->query("DELETE FROM reports WHERE reporter_id = :id", [':id' => $id]);

            // Теперь удаляем самого пользователя
            $sql = "DELETE FROM users WHERE id = :id";
            $stmt = $this->db->query($sql, [':id' => $id]);

            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error deleting user: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Получает всех пользователей.
     *
     * @return array Массив пользователей.
     */
    public function getAll(): array
    {
        $sql = "SELECT id, name, email, reputation, registration_date, role FROM users ORDER BY registration_date DESC";
        $stmt = $this->db->query($sql);
        return $stmt->fetchAll();
    }
}