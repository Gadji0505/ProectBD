<?php

namespace App\Models;

use App\Core\Database;

class Category
{
    protected Database $db;

    public function __construct(Database $db)
    {
        $this->db = $db;
    }

    /**
     * Генерирует уникальный slug из названия категории.
     *
     * @param string $name Название категории.
     * @return string Сгенерированный slug.
     */
    protected function generateSlug(string $name): string
    {
        $slug = strtolower(trim(preg_replace('/[^A-Za-z0-9-]+/', '-', $name), '-'));
        $originalSlug = $slug;
        $counter = 1;

        // Проверяем уникальность slug в БД
        while ($this->findBySlug($slug)) {
            $slug = $originalSlug . '-' . $counter++;
        }
        return $slug;
    }

    /**
     * Создает новую категорию.
     *
     * @param string $name Название категории.
     * @param string $description Описание категории.
     * @return int|false ID новой категории или false при ошибке.
     */
    public function createCategory(string $name, string $description = ''): int|false
    {
        $slug = $this->generateSlug($name);
        $sql = "INSERT INTO categories (name, slug, description) VALUES (:name, :slug, :description)";
        $params = [
            ':name' => $name,
            ':slug' => $slug,
            ':description' => $description
        ];
        try {
            $this->db->query($sql, $params);
            return (int)$this->db->lastInsertId();
        } catch (\PDOException $e) {
            error_log("Error creating category: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Находит категорию по ID.
     *
     * @param int $id ID категории.
     * @return array|false Данные категории или false, если не найдена.
     */
    public function findById(int $id): array|false
    {
        $sql = "SELECT * FROM categories WHERE id = :id";
        $stmt = $this->db->query($sql, [':id' => $id]);
        return $stmt->fetch();
    }

    /**
     * Находит категорию по slug.
     *
     * @param string $slug Slug категории.
     * @return array|false Данные категории или false, если не найдена.
     */
    public function findBySlug(string $slug): array|false
    {
        $sql = "SELECT * FROM categories WHERE slug = :slug";
        $stmt = $this->db->query($sql, [':slug' => $slug]);
        return $stmt->fetch();
    }

    /**
     * Обновляет категорию.
     *
     * @param int $id ID категории.
     * @param string $name Новое название.
     * @param string $description Новое описание.
     * @return bool Успех обновления.
     */
    public function updateCategory(int $id, string $name, string $description): bool
    {
        $sql = "UPDATE categories SET name = :name, description = :description WHERE id = :id";
        $params = [
            ':name' => $name,
            ':description' => $description,
            ':id' => $id
        ];
        try {
            $stmt = $this->db->query($sql, $params);
            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error updating category: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Удаляет категорию по ID.
     * Также удаляет все связи статьи-категории.
     *
     * @param int $id ID категории.
     * @return bool Успех удаления.
     */
    public function deleteCategory(int $id): bool
    {
        try {
            // Удаляем связи из article_categories
            $this->db->query("DELETE FROM article_categories WHERE category_id = :id", [':id' => $id]);

            // Удаляем саму категорию
            $sql = "DELETE FROM categories WHERE id = :id";
            $stmt = $this->db->query($sql, [':id' => $id]);
            return $stmt->rowCount() > 0;
        } catch (\PDOException $e) {
            error_log("Error deleting category: " . $e->getMessage());
            return false;
        }
    }

    /**
     * Получает все категории.
     *
     * @return array Массив категорий.
     */
    public function getAll(): array
    {
        $sql = "SELECT * FROM categories ORDER BY name ASC";
        $stmt = $this->db->query($sql);
        return $stmt->fetchAll();
    }
}