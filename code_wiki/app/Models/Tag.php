<?php

namespace App\Models;

use App\Core\Database;

class Tag
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Создает новый тег.
     *
     * @param string $name Название тега.
     * @return int|false ID нового тега или false при ошибке.
     */
    public function createTag(string $name): int|false
    {
        // Проверяем, существует ли тег, чтобы избежать дубликатов
        $existingTag = $this->findByName($name);
        if ($existingTag) {
            return $existingTag['id']; // Возвращаем существующий ID
        }

        $sql = "INSERT INTO tags (name) VALUES (:name)";
        $params = [':name' => $name];
        try {
            $this->db->query($sql, $params);
            return (int)$this->db->lastInsertId();
        } catch (\PDOException $e) {
            error_log("Error creating tag: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Находит тег по ID.
     *
     * @param int $id ID тега.
     * @return array|false Данные тега или false, если не найден.
     */
    public function findById(int $id): array|false
    {
        $sql = "SELECT * FROM tags WHERE id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->fetch();
    }

    /**
     * Находит тег по названию.
     *
     * @param string $name Название тега.
     * @return array|false Данные тега или false, если не найден.
     */
    public function findByName(string $name): array|false
    {
        $sql = "SELECT * FROM tags WHERE name = :name";
        $stmt = $this->db->query($sql, [':name' => $name]);
        return $stmt->fetch();
    }

    /**
     * Обновляет название тега.
     *
     * @param int $id ID тега.
     * @param string $newName Новое название.
     * @return bool Успех обновления.
     */
    public function updateTag(int $id, string $newName): bool
    {
        $sql = "UPDATE tags SET name = :new_name WHERE id = :id";
        $params = [
            ':new_name' => $newName,
            ':id' => $id
        ];
        try {
            $stmt = $this->db->query($sql, $params);
            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error updating tag: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Удаляет тег по ID.
     * Также удаляет все связи статьи-тега.
     *
     * @param int $id ID тега.
     * @return bool Успех удаления.
     */
    public function deleteTag(int $id): bool
    {
        try {
            // Удаляем связи из article_tags
            $this->db->query("DELETE FROM article_tags WHERE tag_id = :id", [':id' => $id]);

            // Удаляем сам тег
            $sql = "DELETE FROM tags WHERE id = :id";
            $stmt = $this->db->query($sql, [':id' => $id]);
            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error deleting tag: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Получает все теги.
     *
     * @return array Массив тегов.
     */
    public function getAll(): array
    {
        $sql = "SELECT * FROM tags ORDER BY name ASC";
        $stmt = $this->db->query($sql);
        return $stmt->fetchAll();
    }
}